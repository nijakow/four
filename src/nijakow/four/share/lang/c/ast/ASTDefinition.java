package nijakow.four.share.lang.c.ast;

import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.lang.c.SlotVisibility;
import nijakow.four.share.lang.base.parser.StreamPosition;

public abstract class ASTDefinition extends ASTDecl {
	private final SlotVisibility visibility;
	private final Type type;
	private final Key name;
	
	public ASTDefinition(StreamPosition pos, String cDoc, SlotVisibility visibility, Type type, Key name) {
		super(pos, cDoc);
		this.visibility = visibility;
		this.type = type;
		this.name = name;
	}

	protected SlotVisibility getVisibility() { return visibility; }
	protected Type getType() { return type; }
	protected Key getName() { return name; }
}
