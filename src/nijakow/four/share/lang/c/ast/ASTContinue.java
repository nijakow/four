package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.FCompiler;

public class ASTContinue extends ASTInstruction {

	@Override
	void compile(FCompiler compiler) {
		compiler.getContinueLabel().compileJump();
	}
}
