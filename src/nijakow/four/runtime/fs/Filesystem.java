package nijakow.four.runtime.fs;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.parser.ParseException;
import nijakow.four.runtime.Blue;
import nijakow.four.runtime.Blueprint;
import nijakow.four.runtime.FourRuntimeException;

import java.util.LinkedList;
import java.util.List;

public class Filesystem {
	public static final String LIB_FOLDER_NAME = "lib" + java.io.File.separator;
	public static final String SECURE_FOLDER_NAME = "secure";
	public static String WORKING_DIR = LIB_FOLDER_NAME;
	private static final int WORKING_INDEX = 1;
	private final List<Layer> layers;
	private final ResourceLoader loader;

	public Filesystem(ResourceLoader loader) {
		this.loader = loader;
		layers = new LinkedList<>();
		Layer secure = new ImmutableLayer(this);
		Layer std = new ImmutableLayer(this);
		try {
			loadShippedFiles(secure, std);
		} catch (ImmutableException e) {
			e.printStackTrace();
			System.err.println("This should not have happened!");
		}
		layers.add(secure);
		loadSnapshots();
		layers.add(std);
		layers.add(WORKING_INDEX, new DefaultLayer(this));
	}

	public Filesystem() { this(null); }

	private void loadSnapshots() {
		// TODO This method should read a file containing previously created layers
	}

	private void loadShippedFiles(Layer secure, Layer std) throws ImmutableException {
		for (java.io.File file : loader.listFolderContents("")) {
			if (file.isDirectory()) {
				if (file.getName().equals(SECURE_FOLDER_NAME)) {
					secure.insertNode(loadDirectory(file.getName(), file.getName(), secure));
				} else {
					std.insertNode(loadDirectory(file.getName(), file.getName(), std));
				}
			} else {
				std.insertNode(loadFile(file.getName(), std));
			}
		}
	}

	private Directory loadDirectory(String path, String name,  FSNode parent) throws ImmutableException {
		Directory directory = new Directory(this, parent, name);
		for (java.io.File file : loader.listFolderContents(path)) {
			if (file.isDirectory()) {
				directory.insertNode(loadDirectory(path + java.io.File.separator + file.getName(), file.getName(), directory));
			} else {
				directory.insertNode(loadFile(file.getName(), directory));
			}
		}
		return directory;
	}

	private File loadFile(String path, Directory parent) throws ImmutableException {
		String source = loader.getResourceText(parent.getFullName() + "/" + path);
		if (source != null) {
			FSNode node = parent.find(path, true);
			node.asFile().setContents(source);
			return node.asFile();
		} else {
			return null;
		}
	}

	public Snapshot makeSnapshot() {
		layers.add(WORKING_INDEX + 1, layers.get(WORKING_INDEX).createImmutable());
		layers.remove(layers.get(WORKING_INDEX));
		layers.add(WORKING_INDEX, new DefaultLayer(this));
		return new Snapshot(layers.size() - 3, layers.get(WORKING_INDEX + 1));
	}

	private void backToSnapshot(Snapshot snapshot) {
		for (int i = 0; i < snapshot.getNumber(); i++) {
			layers.remove(WORKING_INDEX);
		}
		layers.add(WORKING_INDEX, layers.get(WORKING_INDEX).createMutable());
	}

	public void deleteSnapshot(Snapshot snapshot) throws FourRuntimeException {
		// for each file in that layer: if it has not been overridden, add it to the next layer
		for (File file : layers.get(snapshot.getNumber()).getAllFiles()) {
			boolean found = false;
			for (int i = WORKING_INDEX; i < WORKING_INDEX + snapshot.getNumber(); i++) {
				if (layers.get(i).containsFile(file)) {
					found = true;
					break;
				}
			}
			if (!found) {
				// Copy to nearest newer layer
			}
		}
	}

	public Directory getWorkingLayer() {
		return layers.get(WORKING_INDEX);
	}

	public List<FSNode> listChildren(String name) {
		List<FSNode> list = new LinkedList<>();
		for (Layer layer : layers) {
			FSNode dir = layer.find(name);
			if (dir != null && dir.asDir() != null) {
				list.addAll(dir.asDir().getChildren());
			}
		}
		return list;
	}

	public FSNode find(String text) {
		FSNode node = null;
		for (Layer layer : layers) {
			if ((node = layer.find(text)) != null)
				break;
		}
		return node;
	}

	public FSNode touchf(String fname) throws ImmutableException {
		return getWorkingLayer().find(fname, true);
	}

	public File writeFile(String filePath, String content) throws ImmutableException {
		File file = find(filePath).asFile();
		if (file == null)
			return null;
		if (file.getRoot() instanceof ImmutableLayer) {
			file = touchf(filePath).asFile();
		}
		file.setContents(content);
		return file;
	}

	public Blue getBlueWithErrors(String path) throws ParseException, CompilationException {
		FSNode node = find(path);
		if (node == null) return null;
		return node.asFile().getInstanceWithErrors();
	}

	public Blueprint getBlueprint(String path) throws ParseException, CompilationException {
		FSNode node = find(path);
		if (node == null) return null;
		return node.asFile().getBlueprintWithErrors();
	}

	/*public Blue getBlue(String path) {
		FSNode node = find(path);
		if (node == null) return null;
		return node.asFile().getInstance();
	}
	
	public Blueprint getBlueprint(String path) {
		FSNode node = find(path);
		if (node == null) return null;
		return node.asFile().getBlueprint();
	}*/
}
