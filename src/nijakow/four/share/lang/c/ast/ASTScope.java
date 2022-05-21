package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.server.runtime.Key;
import nijakow.four.share.lang.base.parser.StreamPosition;

public class ASTScope extends ASTExpression {
	private final ASTExpression expr;
	private final ASTExpression[] args;
	private final Key member;
	
	public ASTScope(StreamPosition pos, ASTExpression expr, ASTExpression[] args, Key member) {
		super(pos);
		this.expr = expr;
		this.args = args;
		this.member = member;
	}

	@Override
	public void compile(FCompiler compiler) throws CompilationException {
		for (ASTExpression arg : args) {
			arg.compile(compiler);
			compiler.compilePush();
		}
		compiler.tell(this);
		expr.compile(compiler);
		compiler.compileScope(member, args.length);
	}
}
