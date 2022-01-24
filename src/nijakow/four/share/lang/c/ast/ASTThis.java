package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.FCompiler;

public class ASTThis extends ASTExpression {
	public ASTThis() {}

	@Override
	public void compile(FCompiler compiler) {
		compiler.compileLoadThis();
	}
}
