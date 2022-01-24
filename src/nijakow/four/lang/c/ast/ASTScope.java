package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.CompilationException;
import nijakow.four.lang.c.compiler.FCompiler;
import nijakow.four.server.runtime.Key;

public class ASTScope extends ASTExpression {
	private final ASTExpression expr;
	private final Key member;
	
	public ASTScope(ASTExpression expr, Key member) {
		this.expr = expr;
		this.member = member;
	}

	@Override
	public void compile(FCompiler compiler) throws CompilationException {
		expr.compile(compiler);
		compiler.compileScope(member);
	}
}
