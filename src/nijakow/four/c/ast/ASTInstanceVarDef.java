package nijakow.four.c.ast;

import nijakow.four.runtime.objects.Blueprint;
import nijakow.four.runtime.FourClassLoader;
import nijakow.four.runtime.Key;
import nijakow.four.runtime.types.Type;

public class ASTInstanceVarDef extends ASTDefinition {

	public ASTInstanceVarDef(Type type, Key name) {
		super(type, name);
	}

	@Override
	public void compileInto(Blueprint blueprint, FourClassLoader fs) {
		blueprint.addSlot(getType(), getName());
	}
}
