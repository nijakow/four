package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.CompilationException;
import nijakow.four.lang.c.parser.ParseException;
import nijakow.four.server.runtime.objects.Blueprint;
import nijakow.four.server.runtime.FourClassLoader;

public abstract class ASTDecl extends AST {

	public abstract void compileInto(Blueprint blueprint, FourClassLoader fs) throws ParseException, CompilationException;
}
