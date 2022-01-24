package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.FCompiler;

public class ASTThis extends ASTExpression {
	public ASTThis() {}

	@Override
	public void compile(FCompiler compiler) {
		compiler.compileLoadThis();
	}
}
