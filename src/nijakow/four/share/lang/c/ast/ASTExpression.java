package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.parser.StreamPosition;

public abstract class ASTExpression extends ASTInstruction {

	public ASTExpression(StreamPosition pos) {
		super(pos);
	}

	public void compileCall(FCompiler compiler, int args, boolean hasVarargs) throws CompilationException {
		compiler.tell(this);
		this.compile(compiler);
		compiler.compileCall(args, hasVarargs);
	}

	public void compileAssignment(FCompiler compiler, ASTExpression right) throws CompilationException {
		compilationError("Can not assign to this AST!");
	}

}
