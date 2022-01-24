package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.FCompiler;

public class ASTVaCount extends ASTExpression {
	public ASTVaCount() {}

	@Override
	public void compile(FCompiler compiler) {
		compiler.compileLoadVACount();
	}
}
