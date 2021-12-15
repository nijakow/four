package nijakow.four;

import nijakow.four.c.runtime.Blueprint;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.fs.File;
import nijakow.four.c.runtime.fs.Filesystem;
import nijakow.four.c.runtime.vm.VM;

public class Four {

	public static void main(String[] args) {
		Filesystem fs = new Filesystem();
		File file = fs.getRoot().createDir("src").createFile("test.c").setContents("""
				
any foo() {
	any x;
	
	x = 0;
	while (x <= 42) {
		x.print();
		x = x + 1;
	}
}

any test() {
	foo();
}
		""");
		
		Blueprint bp = file.compile();
		
		VM vm = new VM();
		vm.startFiber(bp.createBlue(), Key.get("test"), new Instance[0]);
		vm.run();
	}
}
