package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.server.runtime.FourClassLoader;
import nijakow.four.server.runtime.Key;
import nijakow.four.share.lang.c.SlotVisibility;
import nijakow.four.share.lang.base.parser.StreamPosition;

public class ASTDefaultDef extends ASTDefinition {

	public ASTDefaultDef(StreamPosition pos, Key name) {
		super(pos, null, SlotVisibility.PRIVATE, null, name);
	}

	@Override
	public void compileInto(Blueprint blueprint, FourClassLoader fs) throws CompilationException {
		Code code = getName().getCode();
		if (code == null)
			compilationError("Import not possible.");
		blueprint.addMethod(getVisibility(), getName(), code);
	}
}
