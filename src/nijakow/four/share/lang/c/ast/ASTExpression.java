package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;

public abstract class ASTExpression extends ASTInstruction {

	public void compileCall(FCompiler compiler, int args, boolean hasVarargs) throws CompilationException {
		this.compile(compiler);
		compiler.compileCall(args, hasVarargs);
	}

	public void compileAssignment(FCompiler compiler, ASTExpression right) throws CompilationException {
		compilationError("Oof.");
	}

}
