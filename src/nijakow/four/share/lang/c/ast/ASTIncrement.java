package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.server.runtime.objects.standard.FInteger;

public class ASTIncrement extends ASTExpression {
	private final ASTExpression expr;
	private final int inc;
	private final boolean post;
	
	public ASTIncrement(ASTExpression expr, int inc, boolean post) {
		this.expr = expr;
		this.inc = inc;
		this.post = post;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		expr.compileAssignment(compiler, new ASTBinOp(OperatorType.PLUS, expr, new ASTConstant(FInteger.get(inc))));
		if (post) {
			compiler.compilePush();
			compiler.compileLoadConstant(FInteger.get(inc));
			compiler.compileOp(OperatorType.MINUS);
		}
	}
}
