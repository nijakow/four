package nijakow.four.c.ast;

import nijakow.four.runtime.Blueprint;
import nijakow.four.runtime.Code;
import nijakow.four.runtime.Key;
import nijakow.four.runtime.fs.Filesystem;

public class ASTDefaultDef extends ASTDefinition {

	public ASTDefaultDef(Key name) {
		super(null, name);
	}

	@Override
	public void compileInto(Blueprint blueprint, Filesystem fs) {
		Code code = getName().getCode();
		if (code == null)
			throw new RuntimeException("Oof. Can't import this code!");
		blueprint.addMethod(getName(), code);
	}
}
