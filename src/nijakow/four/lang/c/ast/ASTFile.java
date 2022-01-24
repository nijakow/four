package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.CompilationException;
import nijakow.four.lang.c.parser.ParseException;
import nijakow.four.runtime.objects.Blueprint;
import nijakow.four.runtime.FourClassLoader;

public class ASTFile extends AST {
	private final ASTDecl[] defs;
	
	public ASTFile(ASTDecl[] defs) {
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
