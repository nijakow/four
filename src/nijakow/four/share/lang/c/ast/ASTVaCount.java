package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.FCompiler;

public class ASTVaCount extends ASTExpression {
	public ASTVaCount() {}

	@Override
	public void compile(FCompiler compiler) {
		compiler.compileLoadVACount();
	}
}
