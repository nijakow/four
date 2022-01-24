package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.c.compiler.CompilationException;
import nijakow.four.share.lang.c.compiler.FCompiler;

public class ASTIndex extends ASTExpression {
	private final ASTExpression expr;
	private final ASTExpression index;
	
	public ASTIndex(ASTExpression expr, ASTExpression index) {
		this.expr = expr;
		this.index = index;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		expr.compile(compiler);
		compiler.compilePush();
		index.compile(compiler);
		compiler.compileIndex();
	}

	@Override
	public void compileAssignment(FCompiler compiler, ASTExpression right) throws CompilationException {
		expr.compile(compiler);
		compiler.compilePush();
		index.compile(compiler);
		compiler.compilePush();
		right.compile(compiler);
		compiler.compileIndexAssign();
	}
}
