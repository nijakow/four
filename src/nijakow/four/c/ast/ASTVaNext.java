package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTVaNext extends ASTExpression {
	public ASTVaNext() {}

	@Override
	public void compile(FCompiler compiler) {
		compiler.compileLoadVANext();
	}
}
