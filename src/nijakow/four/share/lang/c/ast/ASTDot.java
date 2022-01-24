package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.server.runtime.Key;

public class ASTDot extends ASTExpression {
	private final ASTExpression expr;
	private final Key key;
	
	public ASTDot(ASTExpression expr, Key key) {
		this.expr = expr;
		this.key = key;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		expr.compile(compiler);
		compiler.compileDot(key);
	}

	@Override
	public void compileCall(FCompiler compiler, int args, boolean hasVarargs) throws CompilationException {
		expr.compile(compiler);
		compiler.compileDotCall(key, args, hasVarargs);
	}

	@Override
	public void compileAssignment(FCompiler compiler, ASTExpression right) throws CompilationException {
		right.compile(compiler);
		compiler.compilePush();
		expr.compile(compiler);
		compiler.compileDotAssign(key);
	}
}
