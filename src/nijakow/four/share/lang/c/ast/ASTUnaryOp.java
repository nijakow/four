package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTUnaryOp extends ASTExpression {
	private final OperatorType type;
	private final ASTExpression operand;
	
	public ASTUnaryOp(StreamPosition pos, OperatorType type, ASTExpression operand) {
		super(pos);
		if (type == OperatorType.PLUS)
			type = OperatorType.UPLUS;
		else if (type == OperatorType.MINUS)
			type = OperatorType.UMINUS;
		this.type = type;
		this.operand = operand;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler.tell(this);
		operand.compile(compiler);
		compiler.compileOp(type);
	}
}
