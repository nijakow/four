package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTMapping extends ASTExpression {
	private final ASTExpression[] exprs;
	
	public ASTMapping(StreamPosition pos, ASTExpression[] exprs) {
		super(pos);
		this.exprs = exprs;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler.tell(this);
		for (ASTExpression expr : exprs) {
			expr.compile(compiler);
			compiler.compilePush();
		}
		compiler.compileMakeMapping(exprs.length);
	}
}
