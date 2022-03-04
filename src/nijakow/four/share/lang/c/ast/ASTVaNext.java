package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTVaNext extends ASTExpression {
	public ASTVaNext(StreamPosition pos) {
		super(pos);
	}

	@Override
	public void compile(FCompiler compiler) {
		compiler.compileLoadVANext();
	}
}
