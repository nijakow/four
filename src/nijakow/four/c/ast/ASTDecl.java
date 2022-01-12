package nijakow.four.c.ast;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.parser.ParseException;
import nijakow.four.runtime.Blueprint;
import nijakow.four.runtime.fs.Filesystem;

public abstract class ASTDecl {

	public abstract void compileInto(Blueprint blueprint, Filesystem fs) throws ParseException, CompilationException;
}
