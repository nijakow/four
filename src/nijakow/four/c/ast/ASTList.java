package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;
import nijakow.four.runtime.ListType;

public class ASTList extends ASTExpression {
	private final ListType type;
	private final ASTExpression[] exprs;
	
	public ASTList(ListType type, ASTExpression[] exprs) {
		this.type = type;
		this.exprs = exprs;
	}

	@Override
	void compile(FCompiler compiler) {
		for (ASTExpression expr : exprs) {
			expr.compile(compiler);
			compiler.compilePush();
		}
		compiler.compileMakeList(type, exprs.length);
	}
}
