package nijakow.four.c.ast;

import nijakow.four.c.parser.ParseException;
import nijakow.four.runtime.Blueprint;
import nijakow.four.runtime.fs.Filesystem;

public class ASTFile extends AST {
	private final ASTDecl[] defs;
	
	public ASTFile(ASTDecl[] defs) {
		this.defs = defs;
	}

	public Blueprint compile(String filename, Filesystem fs) throws ParseException {
		Blueprint blueprint = new Blueprint(filename);
		
		for (ASTDecl decl : defs) {
			decl.compileInto(blueprint, fs);
		}
		
		return blueprint;
	}
}
