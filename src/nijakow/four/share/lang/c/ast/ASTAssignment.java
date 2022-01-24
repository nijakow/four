package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.c.compiler.CompilationException;
import nijakow.four.share.lang.c.compiler.FCompiler;

public class ASTAssignment extends ASTExpression {
	private final ASTExpression left;
	private final ASTExpression right;
	
	public ASTAssignment(ASTExpression left, ASTExpression right) {
		this.left = left;
		this.right = right;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		left.compileAssignment(compiler, right);
	}
}
