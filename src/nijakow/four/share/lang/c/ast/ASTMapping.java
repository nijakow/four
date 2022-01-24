package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;

public class ASTMapping extends ASTExpression {
	private final ASTExpression[] exprs;
	
	public ASTMapping(ASTExpression[] exprs) {
		this.exprs = exprs;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		for (ASTExpression expr : exprs) {
			expr.compile(compiler);
			compiler.compilePush();
		}
		compiler.compileMakeMapping(exprs.length);
	}
}
