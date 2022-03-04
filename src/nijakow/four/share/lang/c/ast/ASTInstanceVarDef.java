package nijakow.four.share.lang.c.ast;

import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.runtime.FourClassLoader;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.lang.c.SlotVisibility;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTInstanceVarDef extends ASTDefinition {

	public ASTInstanceVarDef(StreamPosition pos, SlotVisibility visibility, Type type, Key name) {
		super(pos, visibility, type, name);
	}

	@Override
	public void compileInto(Blueprint blueprint, FourClassLoader fs) {
		blueprint.addSlot(getVisibility(), getType(), getName());
	}
}
