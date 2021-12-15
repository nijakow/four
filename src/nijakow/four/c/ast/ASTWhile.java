package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;
import nijakow.four.c.compiler.Label;

public class ASTWhile extends ASTInstruction {
	private final ASTExpression condition;
	private final ASTInstruction body;

	public ASTWhile(ASTExpression condition, ASTInstruction body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	void compile(FCompiler compiler) {
		Label start = compiler.openLabel();
		Label end = compiler.openLabel();
		start.place();
		condition.compile(compiler);
		end.compileJumpIfNot();
		body.compile(compiler);
		start.compileJump();
		end.place();
		start.close();
		end.close();
	}
}
