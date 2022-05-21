package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.parser.StreamPosition;

public class ASTThis extends ASTExpression {
	public ASTThis(StreamPosition pos) {
		super(pos);
	}

	@Override
	public void compile(FCompiler compiler) {
		compiler.tell(this);
		compiler.compileLoadThis();
	}
}
