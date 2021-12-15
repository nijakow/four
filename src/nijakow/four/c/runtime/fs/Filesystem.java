package nijakow.four.c.runtime.fs;

public class Filesystem {
	private final Directory root = new Directory(null, "");
	
	public Filesystem() {	
	}
	
	public Directory getRoot() { return root; }
	
	public FSNode find(String text) { return getRoot().find(text); }
	
	
	public void initialize() {
		Directory secure = getRoot().mkdir("secure");
		File secureMaster = secure.touch("master.c");
		
		secureMaster.setContents("any create() { \"Hello, world!\".print(); }");
	}
}
