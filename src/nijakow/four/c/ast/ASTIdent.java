package nijakow.four.c.ast;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.compiler.FCompiler;
import nijakow.four.runtime.Key;

public class ASTIdent extends ASTExpression {
	private final Key identifier;
	
	public ASTIdent(Key identifier) {
		this.identifier = identifier;
	}

	@Override
	void compile(FCompiler compiler) {
		compiler.compileLoadVariable(identifier);
	}

	@Override
	public void compileCall(FCompiler compiler, int args, boolean hasVarargs) throws CompilationException {
		if (compiler.isLocal(identifier)) {
			super.compileCall(compiler, args, hasVarargs);
		} else {
			compiler.compileLoadThis();
			compiler.compileDotCall(identifier, args, hasVarargs);
		}
	}

	@Override
	public void compileAssignment(FCompiler compiler, ASTExpression right) throws CompilationException {
		right.compile(compiler);
		compiler.compileStoreVariable(identifier);
	}
}
