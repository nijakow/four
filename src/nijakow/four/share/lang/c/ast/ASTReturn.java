package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTReturn extends ASTInstruction {
	private final ASTExpression value;
	
	public ASTReturn(StreamPosition pos, ASTExpression value) {
		super(pos);
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
