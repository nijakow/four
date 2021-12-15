package nijakow.four.c.runtime.fs;

public class Filesystem {
	private final Directory root = new Directory(null, "");
	
	public Filesystem() {	
	}
	
	public Directory getRoot() { return root; }
}
