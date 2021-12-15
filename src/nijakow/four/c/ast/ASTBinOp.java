package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTBinOp extends ASTExpression {
	private final OperatorType type;
	private final ASTExpression left;
	private final ASTExpression right;
	
	public ASTBinOp(OperatorType type, ASTExpression left, ASTExpression right) {
		this.type = type;
		this.left = left;
		this.right = right;
	}

	@Override
	void compile(FCompiler compiler) {
		left.compile(compiler);
		compiler.compilePush();
		right.compile(compiler);
		compiler.compileOp(type);
	}
}
