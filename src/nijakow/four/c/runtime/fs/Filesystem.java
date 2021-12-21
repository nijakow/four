package nijakow.four.c.runtime.fs;

import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.Blueprint;

public class Filesystem {
	private final Directory root = new Directory(this, null, "");
	private final ResourceLoader loader;
	
	public Filesystem(ResourceLoader loader) {
		this.loader = loader;
	}
	
	public Filesystem() { this(null); }
	
	public Directory getRoot() { return root; }
	
	public FSNode find(String text) {
		FSNode node = getRoot().find(text);
		if (node == null)
			return loadFile(text);
		else
			return node;
	}
	
	public FSNode touchf(String fname) {
		return getRoot().find(fname, true);
	}

	private File loadFile(String path) {
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
		if (node == null || node.asFile() == null)
			node = loadFile(path);
		if (node == null) return null;
		return node.asFile().getInstance();
	}
	
	public Blueprint getBlueprint(String path) {
		FSNode node = find(path);
		if (node == null || node.asFile() == null)
			node = loadFile(path);
		if (node == null) return null;
		return node.asFile().getBlueprint();
	}
}
