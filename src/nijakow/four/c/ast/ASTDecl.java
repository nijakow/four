package nijakow.four.c.ast;

import nijakow.four.c.runtime.Blueprint;
import nijakow.four.c.runtime.fs.Filesystem;

public abstract class ASTDecl {

	public abstract void compileInto(Blueprint blueprint, Filesystem fs);
}
