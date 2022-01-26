package nijakow.four.share.lang.c.ast;

import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.lang.c.SlotVisibility;

public abstract class ASTDefinition extends ASTDecl {
	private final SlotVisibility visibility;
	private final Type type;
	private final Key name;
	
	public ASTDefinition(SlotVisibility visibility, Type type, Key name) {
		this.visibility = visibility;
		this.type = type;
		this.name = name;
	}

	protected SlotVisibility getVisibility() { return visibility; }
	protected Type getType() { return type; }
	protected Key getName() { return name; }
}
