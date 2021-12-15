package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTCall extends ASTExpression {
	private final ASTExpression expr;
	private final ASTExpression[] args;
	
	public ASTCall(ASTExpression expr, ASTExpression[] args) {
		this.expr = expr;
		this.args = args;
	}

	@Override
	void compile(FCompiler compiler) {
		for (ASTExpression expr : args) {
			expr.compile(compiler);
			compiler.compilePush();
		}
		expr.compileCall(compiler, args.length);
	}
}
