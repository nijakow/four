package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTVaCount extends ASTExpression {
	public ASTVaCount() {}

	@Override
	public void compile(FCompiler compiler) {
		compiler.compileLoadVACount();
	}
}
