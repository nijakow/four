package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTList extends ASTExpression {
	private final ASTExpression[] exprs;
	
	public ASTList(ASTExpression[] exprs) {
		this.exprs = exprs;
	}

	@Override
	void compile(FCompiler compiler) {
		for (ASTExpression expr : exprs) {
			expr.compile(compiler);
			compiler.compilePush();
		}
		compiler.compileMakeList(exprs.length);
	}
}
