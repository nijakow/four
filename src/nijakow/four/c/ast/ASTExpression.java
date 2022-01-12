package nijakow.four.c.ast;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.compiler.FCompiler;

public abstract class ASTExpression extends ASTInstruction {

	public void compileCall(FCompiler compiler, int args, boolean hasVarargs) throws CompilationException {
		this.compile(compiler);
		compiler.compileCall(args, hasVarargs);
	}

	public void compileAssignment(FCompiler compiler, ASTExpression right) throws CompilationException {
		throw new RuntimeException("Oof.");
	}

}
