package nijakow.four.c.ast;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.compiler.FCompiler;

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
