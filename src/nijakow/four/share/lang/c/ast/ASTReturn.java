package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.share.lang.base.parser.StreamPosition;

public class ASTReturn extends ASTInstruction {
	private final ASTExpression value;
	
	public ASTReturn(StreamPosition pos, ASTExpression value) {
		super(pos);
		this.value = value;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler.tell(this);
		if (this.value == null) {
			compiler.compileLoadConstant(Instance.getNil());
		} else {
			value.compile(compiler);
		}
		compiler.tell(this);
		compiler.compileReturn();
	}
}
