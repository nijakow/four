package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTContinue extends ASTInstruction {

	public ASTContinue(StreamPosition pos) {
		super(pos);
	}

	@Override
	void compile(FCompiler compiler) {
		compiler.tell(this);
		compiler.getContinueLabel().compileJump();
	}
}
