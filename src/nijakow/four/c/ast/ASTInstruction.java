package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public abstract class ASTInstruction extends AST {
	void compile(FCompiler compiler) {
		throw new RuntimeException("Oof");
	}
}
