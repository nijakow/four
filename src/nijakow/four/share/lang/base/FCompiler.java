package nijakow.four.share.lang.base;

import nijakow.four.share.lang.c.ast.OperatorType;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.ListType;
import nijakow.four.server.runtime.types.Type;

public interface FCompiler {
	Label openLabel();
	Label openBreakLabel();
	Label openContinueLabel();
	Label getBreakLabel();
	Label getContinueLabel();
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
	
	void compileMakeList(ListType type, int length);
	void compileMakeMapping(int length);
}
