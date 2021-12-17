package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;
import nijakow.four.c.runtime.Key;

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
	public void compileCall(FCompiler compiler, int args, boolean hasVarargs) {
		compiler.compileLoadThis();
		compiler.compileDotCall(identifier, args, hasVarargs);
	}

	@Override
	public void compileAssignment(FCompiler compiler, ASTExpression right) {
		right.compile(compiler);
		compiler.compileStoreVariable(identifier);
	}
}
