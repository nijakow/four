package nijakow.four.c.ast;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.compiler.FCompiler;
import nijakow.four.runtime.Key;

public class ASTRepurpose extends ASTExpression {
	private final ASTExpression expr;
	private final Key member;

	public ASTRepurpose(ASTExpression expr, Key member) {
		this.expr = expr;
		this.member = member;
	}

	@Override
	public void compile(FCompiler compiler) throws CompilationException {
		expr.compile(compiler);
		compiler.compileRepurpose(member);
	}
}
