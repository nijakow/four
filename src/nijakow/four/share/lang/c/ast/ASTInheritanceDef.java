package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.c.compiler.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.runtime.objects.Blueprint;
import nijakow.four.server.runtime.FourClassLoader;

public class ASTInheritanceDef extends ASTDecl {
	private final String path;
	
	public ASTInheritanceDef(String path) {
		this.path = path;
	}

	@Override
	public void compileInto(Blueprint blueprint, FourClassLoader fs) throws ParseException, CompilationException {
		Blueprint bp = fs.load(path);
		if (bp == null)
			compilationError("Blueprint parent not found!");
		blueprint.addSuper(bp);
	}
}
