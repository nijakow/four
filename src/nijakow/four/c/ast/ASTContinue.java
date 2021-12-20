package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTContinue extends ASTInstruction {

	@Override
	void compile(FCompiler compiler) {
		compiler.getContinueLabel().compileJump();
	}
}
