package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;
import nijakow.four.c.runtime.Key;

public class ASTScope extends ASTExpression {
	private final ASTExpression expr;
	private final Key member;
	
	public ASTScope(ASTExpression expr, Key member) {
		this.expr = expr;
		this.member = member;
	}

	@Override
	public void compileCall(FCompiler compiler, int args, boolean hasVarargs) {
		expr.compile(compiler);
		compiler.compilePush();
		compiler.compileLoadThis();
		compiler.compileScopeCall(member, args, hasVarargs);
	}
}
