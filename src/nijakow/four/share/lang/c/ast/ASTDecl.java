package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.runtime.FourClassLoader;
import nijakow.four.share.lang.base.parser.StreamPosition;

public abstract class ASTDecl extends AST {
	private final String cDoc;

	public ASTDecl(StreamPosition pos, String cDoc) {
		super(pos);
		this.cDoc = cDoc;
	}

	protected String getCDoc() { return this.cDoc; }

	public abstract void compileInto(Blueprint blueprint, FourClassLoader fs) throws ParseException, CompilationException;
}
