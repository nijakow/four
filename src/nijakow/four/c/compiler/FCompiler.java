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
	void enableVarargs();
	boolean isLocal(Key identifier);

	void compileLoadThis();
	void compileLoadVANext();
	void compileLoadVACount();
	void compileLoadConstant(Instance value);
	
	void compileLoadVariable(Key identifier);
	void compileStoreVariable(Key identifier);

	void compilePush();
	void compileReturn();

	void compileDot(Key key);
	void compileDotAssign(Key key);
	void compileIndex();
	void compileIndexAssign();
	void compileScope(Key key);

	void compileCall(int args, boolean hasVarargs);
	void compileDotCall(Key key, int args, boolean hasVarargs);
	void compileOp(OperatorType type);
	
	void compileMakeList(int length);
}
