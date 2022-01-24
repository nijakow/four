package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.FCompiler;

public class ASTContinue extends ASTInstruction {

	@Override
	void compile(FCompiler compiler) {
		compiler.getContinueLabel().compileJump();
	}
}
