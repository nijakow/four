package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.runtime.FourClassLoader;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTInheritanceDef extends ASTDecl {
	private final String path;
	
	public ASTInheritanceDef(StreamPosition pos, String path) {
		super(pos);
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
