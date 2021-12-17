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
		
		secureMaster.setContents("use log; use on_connect; use pause; any say_hi() { log(\"Hi!\"); } any receive(any connection) { log(\"New connection!\"); } any create() { on_connect(this, \"receive\"); pause(this, \"say_hi\", 5000); }");
	}
}
