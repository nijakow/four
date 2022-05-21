package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.parser.StreamPosition;

public class ASTVaCount extends ASTExpression {
	public ASTVaCount(StreamPosition pos) {
		super(pos);
	}

	@Override
	public void compile(FCompiler compiler) {
		compiler.tell(this);
		compiler.compileLoadVACount();
	}
}
