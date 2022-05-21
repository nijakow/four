package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.Label;
import nijakow.four.share.lang.base.parser.StreamPosition;

public class ASTIf extends ASTInstruction {
	private final ASTExpression condition;
	private final ASTInstruction ifClause;
	private final ASTInstruction elseClause;
	
	public ASTIf(StreamPosition pos, ASTExpression condition, ASTInstruction ifClause) {
		this(pos, condition, ifClause, null);
	}
	
	public ASTIf(StreamPosition pos, ASTExpression condition, ASTInstruction ifClause, ASTInstruction elseClause) {
		super(pos);
		this.condition = condition;
		this.ifClause = ifClause;
		this.elseClause = elseClause;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler.tell(this);
		Label elseBegin = compiler.openLabel();
		Label end = compiler.openLabel();
		
		condition.compile(compiler);
		elseBegin.compileJumpIfNot();
		ifClause.compile(compiler);
		if (elseClause == null) {
			elseBegin.place();
		} else {
			end.compileJump();
			elseBegin.place();
			elseClause.compile(compiler);
		}
		end.place();
		elseBegin.close();
		end.close();
	}
}
