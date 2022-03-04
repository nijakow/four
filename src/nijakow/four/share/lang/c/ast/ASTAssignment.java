package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTAssignment extends ASTExpression {
	private final ASTExpression left;
	private final ASTExpression right;
	
	public ASTAssignment(StreamPosition pos, ASTExpression left, ASTExpression right) {
		super(pos);
		this.left = left;
		this.right = right;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler.tell(this);
		left.compileAssignment(compiler, right);
	}
}
