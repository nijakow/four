package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.parser.StreamPosition;

public class ASTBreak extends ASTInstruction {

	public ASTBreak(StreamPosition pos) {
		super(pos);
	}

	@Override
	void compile(FCompiler compiler) {
		compiler.tell(this);
		compiler.getBreakLabel().compileJump();
	}
}
