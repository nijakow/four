package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.share.lang.base.parser.StreamPosition;

public class ASTConstant extends ASTExpression {
	private final Instance value;
	
	public ASTConstant(StreamPosition pos, Instance value) {
		super(pos);
		this.value = value;
	}

	@Override
	void compile(FCompiler compiler) {
		compiler.tell(this);
		compiler.compileLoadConstant(this.value);
	}
}
