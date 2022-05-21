package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.parser.StreamPosition;

public class ASTBlock extends ASTInstruction {
	private final ASTInstruction[] instructions;
	
	public ASTBlock(StreamPosition pos, ASTInstruction[] instructions) {
		super(pos);
		this.instructions = instructions;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler.tell(this);
		compiler = compiler.subscope();
		for (ASTInstruction instruction : instructions) {
			instruction.compile(compiler);
		}
	}
}
