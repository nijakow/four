package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.server.runtime.FourClassLoader;
import nijakow.four.server.runtime.Key;
import nijakow.four.share.lang.c.SlotVisibility;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTDefaultDef extends ASTDefinition {

	public ASTDefaultDef(StreamPosition pos, Key name) {
		super(pos, SlotVisibility.PRIVATE, null, name);
	}

	@Override
	public void compileInto(Blueprint blueprint, FourClassLoader fs) throws CompilationException {
		Code code = getName().getCode();
		if (code == null)
			throw new CompilationException("Oof. Can't import this code!");
		blueprint.addMethod(getVisibility(), getName(), code);
	}
}
