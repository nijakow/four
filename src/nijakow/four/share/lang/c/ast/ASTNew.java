package nijakow.four.share.lang.c.ast;

import nijakow.four.server.runtime.Key;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTNew extends ASTExpression {
	private final Key clazz;
	private final ASTExpression[] args;
	private final boolean hasVarargs;

	public ASTNew(StreamPosition pos, Key clazz, ASTExpression[] args, boolean hasVarargs) {
		super(pos);
		this.clazz = clazz;
		this.args = args;
		this.hasVarargs = hasVarargs;
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler.tell(this);
		for (ASTExpression expr : args) {
			expr.compile(compiler);
			compiler.compilePush();
		}
		compiler.compileNew(clazz, args.length, hasVarargs);
	}
}
