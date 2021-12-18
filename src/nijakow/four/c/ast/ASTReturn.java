package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;
import nijakow.four.c.runtime.Instance;

public class ASTReturn extends ASTInstruction {
	private final ASTExpression value;
	
	public ASTReturn(ASTExpression value) {
		this.value = value;
	}

	@Override
	void compile(FCompiler compiler) {
		if (this.value == null) {
			compiler.compileLoadConstant(Instance.getNil());
		} else {
			value.compile(compiler);
		}
		compiler.compileReturn();
	}
}
