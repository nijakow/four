package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.FCompiler;

public class ASTVaNext extends ASTExpression {
	public ASTVaNext() {}

	@Override
	public void compile(FCompiler compiler) {
		compiler.compileLoadVANext();
	}
}