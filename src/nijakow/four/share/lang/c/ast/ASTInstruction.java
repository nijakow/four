package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.c.compiler.CompilationException;
import nijakow.four.share.lang.c.compiler.FCompiler;

public abstract class ASTInstruction extends AST {
	void compile(FCompiler compiler) throws CompilationException {
		compilationError("Oof.");
	}
}
