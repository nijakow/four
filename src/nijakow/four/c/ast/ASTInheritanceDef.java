package nijakow.four.c.ast;

import nijakow.four.c.runtime.Blueprint;
import nijakow.four.c.runtime.fs.Filesystem;

public class ASTInheritanceDef extends ASTDecl {
	private final String path;
	
	public ASTInheritanceDef(String path) {
		this.path = path;
	}

	@Override
	public void compileInto(Blueprint blueprint, Filesystem fs) {
		Blueprint bp = fs.getBlueprint(path);
		if (bp == null)
			throw new RuntimeException("Blueprint parent not found!");
		blueprint.addSuper(bp);
	}
}