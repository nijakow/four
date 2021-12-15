package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.Type;

public class ASTVarDecl extends ASTInstruction {
	private final Type type;
	private final Key name;
	
	public ASTVarDecl(Type type, Key name) {
		this.type = type;
		this.name = name;
	}

	@Override
	void compile(FCompiler compiler) {
		compiler.addLocal(type, name);
	}
}
