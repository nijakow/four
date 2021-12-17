package nijakow.four.c.ast;

import nijakow.four.c.runtime.Blueprint;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.Type;
import nijakow.four.c.runtime.fs.Filesystem;

public class ASTInstanceVarDef extends ASTDefinition {

	public ASTInstanceVarDef(Type type, Key name) {
		super(type, name);
	}

	@Override
	public void compileInto(Blueprint blueprint, Filesystem fs) {
		blueprint.addSlot(getType(), getName());
	}
}
