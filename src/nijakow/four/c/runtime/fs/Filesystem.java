package nijakow.four.c.runtime.fs;

public class Filesystem {
	private final Directory root = new Directory(null, "");
	
	public Filesystem() {	
	}
	
	public Directory getRoot() { return root; }
	
	public FSNode find(String text) { return getRoot().find(text); }
}
