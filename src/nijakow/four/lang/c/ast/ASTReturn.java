package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.CompilationException;
import nijakow.four.lang.c.compiler.FCompiler;
import nijakow.four.server.runtime.objects.Instance;

public class ASTReturn extends ASTInstruction {
	private final ASTExpression value;
	
	public ASTReturn(ASTExpression value) {
		this.value = value;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		if (this.value == null) {
			compiler.compileLoadConstant(Instance.getNil());
		} else {
			value.compile(compiler);
		}
		compiler.compileReturn();
	}
}
