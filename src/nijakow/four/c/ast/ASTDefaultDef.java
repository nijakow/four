package nijakow.four.c.ast;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.runtime.objects.Blueprint;
import nijakow.four.runtime.vm.code.Code;
import nijakow.four.runtime.FourClassLoader;
import nijakow.four.runtime.Key;

public class ASTDefaultDef extends ASTDefinition {

	public ASTDefaultDef(Key name) {
		super(null, name);
	}

	@Override
	public void compileInto(Blueprint blueprint, FourClassLoader fs) throws CompilationException {
		Code code = getName().getCode();
		if (code == null)
			throw new CompilationException("Oof. Can't import this code!");
		blueprint.addMethod(getName(), code);
	}
}
