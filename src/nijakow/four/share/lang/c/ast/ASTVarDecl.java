package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTVarDecl extends ASTInstruction {
	private final Type type;
	private final Key name;
	private final ASTExpression value;
	
	public ASTVarDecl(StreamPosition pos, Type type, Key name, ASTExpression value) {
		super(pos);
		this.type = type;
		this.name = name;
		this.value = value;
	}
	
	public ASTVarDecl(StreamPosition pos, Type type, Key name) {
		this(pos, type, name, null);
	}

	@Override
	void compile(FCompiler compiler) throws CompilationException {
		compiler.tell(this);
		if (value != null) {
			value.compile(compiler);
			compiler.addLocal(type, name);
			compiler.compileStoreVariable(name);
		} else {
			compiler.addLocal(type, name);
		}
	}
}
