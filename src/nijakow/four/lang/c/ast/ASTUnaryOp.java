package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.CompilationException;
import nijakow.four.lang.c.compiler.FCompiler;

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
