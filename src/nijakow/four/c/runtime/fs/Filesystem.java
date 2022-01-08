package nijakow.four.c.runtime.fs;

import nijakow.four.Four;
import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.Blueprint;

import java.util.LinkedList;
import java.util.List;

public class Filesystem {
	private static final int WORKING_INDEX = 1;
	private final List<Layer> layers;
	private final ResourceLoader loader;

	public Filesystem(ResourceLoader loader) {
		this.loader = loader;
		layers = new LinkedList<>();
		Layer secure = new ImmutableLayer(this);
		Layer std = new ImmutableLayer(this);
		loadShippedFiles(secure, std);
		layers.add(secure);
		loadSnapshots();
		layers.add(std);
		layers.add(WORKING_INDEX, new DefaultLayer(this));
	}
	
	public Filesystem() { this(null); }

	private void loadSnapshots() {
		// TODO This method should read a file containing previously created layers
	}

	private void loadShippedFiles(Layer secure, Layer std) {
		for (java.io.File file : loader.listFolderContents("")) {
			if (file.isDirectory()) {
				if (file.getName().equals(Four.SECURE_FOLDER_NAME)) {
					secure.insertNode(loadDirectory(file.getName(), file.getName(), secure));
				} else {
					std.insertNode(loadDirectory(file.getName(), file.getName(), std));
				}
			} else {
				std.insertNode(loadFile(file.getName(), std));
			}
		}
	}

	public void makeSnapshot() {
		layers.add(WORKING_INDEX + 1, layers.get(WORKING_INDEX).createImmutable());
		layers.remove(layers.get(WORKING_INDEX));
		layers.add(WORKING_INDEX, new DefaultLayer(this));
	}

	public Directory getRoot() {
		return layers.get(WORKING_INDEX);
	}
	
	public FSNode find(String text) {
		FSNode node = null;
		for (Layer layer : layers) {
			if ((node = layer.find(text)) != null)
				break;
		}
		return node;
	}

	public File writeFile(String filePath, String content) {
		File file = find(filePath).asFile();
		if (file == null)
			return null;
		if (file.getRoot() instanceof ImmutableLayer) {
			file = touchf(filePath).asFile();
		}
		file.setContents(content);
		return file;
	}

	public FSNode touchf(String fname) {
		return getRoot().find(fname, true);
	}

	private Directory loadDirectory(String path, String name,  FSNode parent) {
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

	private File loadFile(String path, Directory parent) {
		String source = loader.getResourceText(parent.getFullName() + "/" + path);
		if (source != null) {
			FSNode node = parent.find(path, true);
			node.asFile().setContents(source);
			return node.asFile();
		} else {
			return null;
		}
	}

	public Blue getBlue(String path) {
		FSNode node = find(path);
		if (node == null) return null;
		return node.asFile().getInstance();
	}
	
	public Blueprint getBlueprint(String path) {
		FSNode node = find(path);
		if (node == null) return null;
		return node.asFile().getBlueprint();
	}
}
