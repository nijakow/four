package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.FCompiler;

public class ASTBreak extends ASTInstruction {

	@Override
	void compile(FCompiler compiler) {
		compiler.getBreakLabel().compileJump();
	}
}
