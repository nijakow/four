package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.c.compiler.FCompiler;
import nijakow.four.server.runtime.objects.Instance;

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
