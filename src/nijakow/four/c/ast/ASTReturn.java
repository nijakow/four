package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTReturn extends ASTInstruction {
	private final ASTExpression value;
	
	public ASTReturn(ASTExpression value) {
		this.value = value;
	}

	@Override
	void compile(FCompiler compiler) {
		value.compile(compiler);
		compiler.compileReturn();
	}
}
