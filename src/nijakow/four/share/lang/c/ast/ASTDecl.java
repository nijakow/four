package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.runtime.FourClassLoader;

public abstract class ASTDecl extends AST {

	public abstract void compileInto(Blueprint blueprint, FourClassLoader fs) throws ParseException, CompilationException;
}
