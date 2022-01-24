package nijakow.four.lang.c.ast;

import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;

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
