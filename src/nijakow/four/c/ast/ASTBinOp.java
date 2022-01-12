package nijakow.four.c.ast;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.compiler.FCompiler;
import nijakow.four.c.compiler.Label;

public class ASTBinOp extends ASTExpression {
	private final OperatorType type;
	private final ASTExpression left;
	private final ASTExpression right;
	
	public ASTBinOp(OperatorType type, ASTExpression left, ASTExpression right) {
		this.type = type;
		this.left = left;
		this.right = right;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		switch (type) {
		case LOGAND: {
			Label label = compiler.openLabel();
			left.compile(compiler);
			label.compileJumpIfNot();
			right.compile(compiler);
			label.place();
			label.close();
			break;
		}
		case LOGOR: {
			/*
			 * This construction is a bit ugly. We don't have a
			 * "jump if" instruction yet, therefore we invert
			 * the condition by adding another label to the code.
			 *                                   - nijakow
			 */
			Label label = compiler.openLabel();
			Label end = compiler.openLabel();
			left.compile(compiler);
			label.compileJumpIfNot();
			end.compileJump();
			label.place();
			right.compile(compiler);
			end.place();
			label.close();
			end.close();
			break;
		}
		default: {
			left.compile(compiler);
			compiler.compilePush();
			right.compile(compiler);
			compiler.compileOp(type);
			break;
		}
		}
	}
}
