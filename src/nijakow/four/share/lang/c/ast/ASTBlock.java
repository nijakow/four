package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;

public class ASTBlock extends ASTInstruction {
	private final ASTInstruction[] instructions;
	
	public ASTBlock(ASTInstruction[] instructions) {
		this.instructions = instructions;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler = compiler.subscope();
		for (ASTInstruction instruction : instructions) {
			instruction.compile(compiler);
		}
	}
}
