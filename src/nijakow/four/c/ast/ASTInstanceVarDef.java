package nijakow.four.c.ast;

import nijakow.four.runtime.Blueprint;
import nijakow.four.runtime.FourClassLoader;
import nijakow.four.runtime.Key;
import nijakow.four.runtime.Type;
import nijakow.four.runtime.fs.Filesystem;

public class ASTInstanceVarDef extends ASTDefinition {

	public ASTInstanceVarDef(Type type, Key name) {
		super(type, name);
	}

	@Override
	public void compileInto(Blueprint blueprint, FourClassLoader fs) {
		blueprint.addSlot(getType(), getName());
	}
}
