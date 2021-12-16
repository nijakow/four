package nijakow.four.c.ast;

import nijakow.four.c.compiler.ScopedCompiler;
import nijakow.four.c.runtime.Blueprint;
import nijakow.four.c.runtime.Code;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.Type;
import nijakow.four.util.Pair;

public class ASTDefaultDef extends ASTDefinition {

	public ASTDefaultDef(Key name) {
		super(null, name);
	}

	@Override
	public void compileInto(Blueprint blueprint) {
		Code code = getName().getCode();
		if (code == null)
			throw new RuntimeException("Oof. Can't import this code!");
		blueprint.addMethod(getName(), code);
	}
}
