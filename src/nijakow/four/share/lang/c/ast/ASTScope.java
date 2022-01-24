package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
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
