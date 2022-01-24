package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.FCompiler;

public class ASTVaNext extends ASTExpression {
	public ASTVaNext() {}

	@Override
	public void compile(FCompiler compiler) {
		compiler.compileLoadVANext();
	}
}
