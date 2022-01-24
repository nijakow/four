package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.CompilationException;
import nijakow.four.lang.c.compiler.FCompiler;
import nijakow.four.lang.c.compiler.Label;

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
