package nijakow.four.c.runtime.fs;

import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.Blueprint;

public class Filesystem {
	private final Directory root = new Directory(this, null, "");
	
	public Filesystem() {	
	}
	
	public Directory getRoot() { return root; }
	
	public FSNode find(String text) { return getRoot().find(text); }
	
	public FSNode touchf(String fname) {
		return getRoot().find(fname, true);
	}

	public Blue getBlue(String path) {
		FSNode node = find(path);
		if (node == null || node.asFile() == null)
			return null;
		else
			return node.asFile().getInstance();
	}
	
	public Blueprint getBlueprint(String path) {
		FSNode node = find(path);
		if (node == null || node.asFile() == null)
			return null;
		else
			return node.asFile().getBlueprint();
	}
}
