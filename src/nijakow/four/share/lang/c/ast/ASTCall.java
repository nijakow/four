package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.parser.StreamPosition;

public class ASTCall extends ASTExpression {
	private final ASTExpression expr;
	private final ASTExpression[] args;
	private final boolean hasVarargs;
	
	public ASTCall(StreamPosition pos, ASTExpression expr, ASTExpression[] args, boolean hasVarargs) {
		super(pos);
		this.expr = expr;
		this.args = args;
		this.hasVarargs = hasVarargs;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler.tell(this);
		for (ASTExpression expr : args) {
			expr.compile(compiler);
			compiler.compilePush();
		}
		compiler.tell(this);
		expr.compileCall(compiler, args.length, hasVarargs);
	}
}
