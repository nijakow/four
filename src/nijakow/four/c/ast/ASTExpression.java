package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public abstract class ASTExpression extends ASTInstruction {

	public void compileCall(FCompiler compiler, int args) {
		throw new RuntimeException("Oof.");
	}

	public void compileAssignment(FCompiler compiler, ASTExpression right) {
		throw new RuntimeException("Oof.");
	}

}
