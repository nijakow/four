package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.Type;

public class ASTVarDecl extends ASTInstruction {
	private final Type type;
	private final Key name;
	private final ASTExpression value;
	
	public ASTVarDecl(Type type, Key name, ASTExpression value) {
		this.type = type;
		this.name = name;
		this.value = value;
	}
	
	public ASTVarDecl(Type type, Key name) {
		this(type, name, null);
	}

	@Override
	void compile(FCompiler compiler) {
		if (value != null) {
			value.compile(compiler);
			compiler.addLocal(type, name);
			compiler.compileStoreVariable(name);
		} else {
			compiler.addLocal(type, name);
		}
	}
}
