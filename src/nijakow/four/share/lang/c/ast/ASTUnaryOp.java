package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;

public class ASTUnaryOp extends ASTExpression {
	private final OperatorType type;
	private final ASTExpression operand;
	
	public ASTUnaryOp(OperatorType type, ASTExpression operand) {
		if (type == OperatorType.PLUS)
			type = OperatorType.UPLUS;
		else if (type == OperatorType.MINUS)
			type = OperatorType.UMINUS;
		this.type = type;
		this.operand = operand;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		operand.compile(compiler);
		compiler.compileOp(type);
	}
}
