package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.runtime.objects.Blueprint;
import nijakow.four.server.runtime.FourClassLoader;

public class ASTClass extends AST {
	private final ASTDecl[] defs;
	
	public ASTClass(ASTDecl[] defs) {
		this.defs = defs;
	}

	public Blueprint compile(String filename, FourClassLoader fs) throws ParseException, CompilationException {
		Blueprint blueprint = new Blueprint(filename);
		
		for (ASTDecl decl : defs) {
			decl.compileInto(blueprint, fs);
		}
		
		return blueprint;
	}
}
