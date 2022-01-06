package nijakow.four.c.runtime.fs;

import nijakow.four.Four;
import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.Blueprint;
import nijakow.four.c.runtime.fs.layers.DefaultLayer;
import nijakow.four.c.runtime.fs.layers.ImmutableLayer;
import nijakow.four.c.runtime.fs.layers.Layer;

import java.util.LinkedList;
import java.util.List;

public class Filesystem {
	private static final int WORKING_INDEX = 1;
	private final List<Layer> layers;
	//private final Directory root = new Directory(this, null, "");
	private final ResourceLoader loader;
	
	public Filesystem(ResourceLoader loader) {
		System.out.println(Four.WORKING_DIR);
		this.loader = loader;
		layers = new LinkedList<>();
		layers.add(loadSecureDir());
		loadSnapshots();
		layers.add(loadStdlib());
		layers.add(WORKING_INDEX, new DefaultLayer(this));
	}
	
	public Filesystem() { this(null); }

	private void loadSnapshots() {
		// TODO This method should read a file containing previously created layers
	}

	private Layer loadSecureDir() {
		// TODO This method should load all files inside the /secure directory
		Layer ret = new ImmutableLayer(this);
		ret.insertNode(loadDirectory("secure", ret));
		return ret;
	}

	private Layer loadStdlib() {
		// TODO This method should load the standard library of the system
		return null;
	}

	public void makeSnapshot() {
		// TODO This method should create a snapshot of the current working layer
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
		//FSNode node = getRoot().find(text);
		/*if (node == null)
			return loadFile(text);
		else*/
		return node;
	}
	
	public FSNode touchf(String fname) {
		return getRoot().find(fname, true);
	}

	private Directory loadDirectory(String path, FSNode parent) {
		Directory directory = new Directory(this, parent, path);
		for (java.io.File file : loader.listFolderContents(path)) {
			if (file.isDirectory()) {
				directory.insertNode(loadDirectory(parent.getFullName() + java.io.File.separator + file.getName(), directory));
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

	private File loadFileOLD(String path) {
		String source = loader.getResourceText(path);
		if (source != null) {
			FSNode node = touchf(path);
			node.asFile().setContents(source);
			return node.asFile();
		} else {
			return null;
		}
	}
	
	public Blue getBlue(String path) {
		FSNode node = find(path);
		/*if (node == null || node.asFile() == null)
			node = loadFile(path);*/
		if (node == null) return null;
		return node.asFile().getInstance();
	}
	
	public Blueprint getBlueprint(String path) {
		FSNode node = find(path);
		/*if (node == null || node.asFile() == null)
			node = loadFile(path);*/
		if (node == null) return null;
		return node.asFile().getBlueprint();
	}
}
