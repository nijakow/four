package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;

public class ASTCall extends ASTExpression {
	private final ASTExpression expr;
	private final ASTExpression[] args;
	private final boolean hasVarargs;
	
	public ASTCall(ASTExpression expr, ASTExpression[] args, boolean hasVarargs) {
		this.expr = expr;
		this.args = args;
		this.hasVarargs = hasVarargs;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		for (ASTExpression expr : args) {
			expr.compile(compiler);
			compiler.compilePush();
		}
		expr.compileCall(compiler, args.length, hasVarargs);
	}
}
