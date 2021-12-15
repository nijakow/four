package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;
import nijakow.four.c.compiler.Label;

public class ASTIf extends ASTInstruction {
	private final ASTExpression condition;
	private final ASTInstruction ifClause;
	private final ASTInstruction elseClause;
	
	public ASTIf(ASTExpression condition, ASTInstruction ifClause) {
		this(condition, ifClause, null);
	}
	
	public ASTIf(ASTExpression condition, ASTInstruction ifClause, ASTInstruction elseClause) {
		this.condition = condition;
		this.ifClause = ifClause;
		this.elseClause = elseClause;
	}

	@Override
	void compile(FCompiler compiler) {
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
