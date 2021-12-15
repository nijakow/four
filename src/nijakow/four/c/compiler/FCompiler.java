package nijakow.four.c.compiler;

import nijakow.four.c.ast.OperatorType;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.Type;

public interface FCompiler {
	Label openLabel();
	FCompiler subscope();
	
	void addParam(Type type, Key name);
	void addLocal(Type type, Key name);

	void compileLoadThis();
	void compileLoadConstant(Instance value);
	
	void compileLoadVariable(Key identifier);
	void compileStoreVariable(Key identifier);

	void compilePush();
	void compileReturn();

	void compileDot(Key key);
	void compileDotAssign(Key key);

	void compileDotCall(Key key, int args);
	void compileOp(OperatorType type);
}
