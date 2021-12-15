package nijakow.four.c.ast;

import nijakow.four.c.runtime.Blueprint;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.Type;

public class ASTInstanceVarDef extends ASTDefinition {

	public ASTInstanceVarDef(Type type, Key name) {
		super(type, name);
	}

	@Override
	public void compileInto(Blueprint blueprint) {
		// TODO Auto-generated method stub
		
	}
}
