package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTThis extends ASTExpression {
	public ASTThis() {}

	@Override
	public void compile(FCompiler compiler) {
		compiler.compileLoadThis();
	}
}
