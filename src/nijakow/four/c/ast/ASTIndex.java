package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;

public class ASTIndex extends ASTExpression {
	private final ASTExpression expr;
	private final ASTExpression index;
	
	public ASTIndex(ASTExpression expr, ASTExpression index) {
		this.expr = expr;
		this.index = index;
	}

	@Override
	void compile(FCompiler compiler) {
		expr.compile(compiler);
		compiler.compilePush();
		index.compile(compiler);
		compiler.compileIndex();
	}

	@Override
	public void compileAssignment(FCompiler compiler, ASTExpression right) {
		expr.compile(compiler);
		compiler.compilePush();
		index.compile(compiler);
		compiler.compilePush();
		right.compile(compiler);
		compiler.compileIndexAssign();
	}
}
