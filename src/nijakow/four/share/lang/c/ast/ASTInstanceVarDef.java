package nijakow.four.share.lang.c.ast;

import nijakow.four.server.runtime.objects.Blueprint;
import nijakow.four.server.runtime.FourClassLoader;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;

public class ASTInstanceVarDef extends ASTDefinition {

	public ASTInstanceVarDef(Type type, Key name) {
		super(type, name);
	}

	@Override
	public void compileInto(Blueprint blueprint, FourClassLoader fs) {
		blueprint.addSlot(getType(), getName());
	}
}
