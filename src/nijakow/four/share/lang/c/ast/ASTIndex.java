package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTIndex extends ASTExpression {
	private final ASTExpression expr;
	private final ASTExpression index;
	
	public ASTIndex(StreamPosition pos, ASTExpression expr, ASTExpression index) {
		super(pos);
		this.expr = expr;
		this.index = index;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler.tell(this);
		expr.compile(compiler);
		compiler.compilePush();
		index.compile(compiler);
		compiler.compileIndex();
	}

	@Override
	public void compileAssignment(FCompiler compiler, ASTExpression right) throws CompilationException {
		compiler.tell(this);
		expr.compile(compiler);
		compiler.compilePush();
		index.compile(compiler);
		compiler.compilePush();
		right.compile(compiler);
		compiler.compileIndexAssign();
	}
}
