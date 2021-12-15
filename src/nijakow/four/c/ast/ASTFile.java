package nijakow.four.c.ast;

import nijakow.four.c.runtime.Blueprint;

public class ASTFile extends AST {
	private final ASTDefinition[] defs;
	
	public ASTFile(ASTDefinition[] defs) {
		this.defs = defs;
	}

	public Blueprint compile() {
		Blueprint blueprint = new Blueprint();
		
		for (ASTDefinition def : defs) {
			def.compileInto(blueprint);
		}
		
		return blueprint;
	}
}
