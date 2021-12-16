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
		
		secureMaster.setContents("""

use say_hi;

any create() {
    any x;
    
    for (x = 0; x < 10; x = x + 1) {
		if (x % 3 == 0) {
			"Durch drei!".print();
		} else {
			x.print();
			(x * x).print();
		}
    }
    say_hi();
}		
		""");
	}
}
