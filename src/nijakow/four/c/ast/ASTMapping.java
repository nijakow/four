package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTMapping extends ASTExpression {
	private final ASTExpression[] exprs;
	
	public ASTMapping(ASTExpression[] exprs) {
		this.exprs = exprs;
	}

	@Override
	void compile(FCompiler compiler) {
		for (ASTExpression expr : exprs) {
			expr.compile(compiler);
			compiler.compilePush();
		}
		compiler.compileMakeMapping(exprs.length);
	}
}
