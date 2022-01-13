package nijakow.four.c.ast;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.parser.ParseException;
import nijakow.four.runtime.Blueprint;
import nijakow.four.runtime.FourClassLoader;
import nijakow.four.runtime.fs.Filesystem;

public abstract class ASTDecl extends AST {

	public abstract void compileInto(Blueprint blueprint, FourClassLoader fs) throws ParseException, CompilationException;
}
