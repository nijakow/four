package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.runtime.FourClassLoader;
import nijakow.four.share.lang.c.parser.StreamPosition;

public abstract class ASTDecl extends AST {

	public ASTDecl(StreamPosition pos) {
		super(pos);
	}

	public abstract void compileInto(Blueprint blueprint, FourClassLoader fs) throws ParseException, CompilationException;
}
