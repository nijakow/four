package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.Label;

public class ASTWhile extends ASTInstruction {
	private final ASTExpression condition;
	private final ASTInstruction body;

	public ASTWhile(ASTExpression condition, ASTInstruction body) {
		this.condition = condition;
		this.body = body;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler = compiler.subscope();
		Label breakLabel = compiler.openBreakLabel();
		Label continueLabel = compiler.openContinueLabel();
		Label start = compiler.openLabel();
		Label end = compiler.openLabel();
		start.place();
		continueLabel.place();
		condition.compile(compiler);
		end.compileJumpIfNot();
		body.compile(compiler);
		start.compileJump();
		end.place();
		breakLabel.place();
		start.close();
		end.close();
	}
}