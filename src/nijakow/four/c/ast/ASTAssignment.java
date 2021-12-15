package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTAssignment extends ASTExpression {
	private final ASTExpression left;
	private final ASTExpression right;
	
	public ASTAssignment(ASTExpression left, ASTExpression right) {
		this.left = left;
		this.right = right;
	}

	@Override
	void compile(FCompiler compiler) {
		left.compileAssignment(compiler, right);
	}
}
