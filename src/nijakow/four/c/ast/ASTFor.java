package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;
import nijakow.four.c.compiler.Label;

public class ASTFor extends ASTInstruction {
	private final ASTInstruction initialization;
	private final ASTExpression condition;
	private final ASTExpression update;
	private final ASTInstruction body;

	public ASTFor(ASTInstruction init, ASTExpression condition, ASTExpression update, ASTInstruction body) {
		this.initialization = init;
		this.condition = condition;
		this.update = update;
		this.body = body;
	}

	@Override
	void compile(FCompiler compiler) {
		compiler = compiler.subscope();
		Label start = compiler.openLabel();
		Label breakPos = compiler.openLabel();
		Label end = compiler.openLabel();
		initialization.compile(compiler);
		start.place();
		condition.compile(compiler);
		end.compileJumpIfNot();
		body.compile(compiler);
		breakPos.place();
		update.compile(compiler);
		start.compileJump();
		end.place();
		start.close();
		breakPos.close();
		end.close();
	}
}
