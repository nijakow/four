package nijakow.four.c.ast;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.compiler.FCompiler;

public abstract class ASTInstruction extends AST {
	void compile(FCompiler compiler) throws CompilationException {
		compilationError("Oof.");
	}
}
