package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.c.compiler.CompilationException;
import nijakow.four.share.lang.c.compiler.FCompiler;

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
