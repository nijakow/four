package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;
import nijakow.four.c.runtime.Instance;

public class ASTConstant extends ASTExpression {
	private final Instance value;
	
	public ASTConstant(Instance value) {
		this.value = value;
	}

	@Override
	void compile(FCompiler compiler) {
		compiler.compileLoadConstant(this.value);
	}
}
