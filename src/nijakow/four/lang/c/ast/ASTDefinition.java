package nijakow.four.lang.c.ast;

import nijakow.four.runtime.Key;
import nijakow.four.runtime.types.Type;

public abstract class ASTDefinition extends ASTDecl {
	private final Type type;
	private final Key name;
	
	public ASTDefinition(Type type, Key name) {
		this.type = type;
		this.name = name;
	}

	protected Type getType() { return type; }
	protected Key getName() { return name; }
}
