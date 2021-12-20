package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTBreak extends ASTInstruction {

	@Override
	void compile(FCompiler compiler) {
		compiler.getBreakLabel().compileJump();
	}
}
