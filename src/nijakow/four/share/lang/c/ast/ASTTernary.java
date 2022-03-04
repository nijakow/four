package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.Label;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTTernary extends ASTExpression {
	private final ASTExpression condition;
	private final ASTExpression ifClause;
	private final ASTExpression elseClause;

	public ASTTernary(StreamPosition pos, ASTExpression condition, ASTExpression ifClause, ASTExpression elseClause) {
		super(pos);
		this.condition = condition;
		this.ifClause = ifClause;
		this.elseClause = elseClause;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		Label elseBegin = compiler.openLabel();
		Label end = compiler.openLabel();
		
		condition.compile(compiler);
		elseBegin.compileJumpIfNot();
		ifClause.compile(compiler);
		end.compileJump();
		elseBegin.place();
		elseClause.compile(compiler);
		end.place();
		elseBegin.close();
		end.close();
	}
}
