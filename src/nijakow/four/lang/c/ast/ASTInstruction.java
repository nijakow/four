package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.CompilationException;
import nijakow.four.lang.c.compiler.FCompiler;

public abstract class ASTInstruction extends AST {
	void compile(FCompiler compiler) throws CompilationException {
		compilationError("Oof.");
	}
}
