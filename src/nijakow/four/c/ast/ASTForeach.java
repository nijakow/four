package nijakow.four.c.ast;

import nijakow.four.c.compiler.FCompiler;
import nijakow.four.c.compiler.Label;
import nijakow.four.c.runtime.FInteger;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.Type;

public class ASTForeach extends ASTInstruction {
	private final Type type;
	private final Key var;
	private final ASTInstruction initialization;
	private final ASTInstruction body;

	public ASTForeach(Type type, Key var, ASTExpression initialization, ASTInstruction body) {
		this.type = type;
		this.var = var;
		this.initialization = initialization;
		this.body = body;
	}

	@Override
	void compile(FCompiler compiler) {
		/*
		 * TODO, FIXME, XXX:
		 * The compiled bytecode assumes that the "length" function
		 * exists. This is a very ugly solution, and it should
		 * be fixed. However, the implementation works just fine
		 * in the current configuration.       - nijakow
		 */
		final Key indexVarName = Key.newGensym();
		final Type indexVarType = Type.getInt();
		final Key listVarName = Key.newGensym();
		final Type listVarType = Type.getAny().listType();

		compiler = compiler.subscope();

		compiler.addLocal(type, var);
		compiler.addLocal(indexVarType, indexVarName);
		compiler.addLocal(listVarType, listVarName);

		Label breakLabel = compiler.openBreakLabel();
		Label continueLabel = compiler.openContinueLabel();

		Label start = compiler.openLabel();
		Label end = compiler.openLabel();

		compiler.compileLoadConstant(new FInteger(0));
		compiler.compileStoreVariable(indexVarName);
		initialization.compile(compiler);
		compiler.compileStoreVariable(listVarName);

		start.place();
		compiler.compileLoadVariable(indexVarName);
		compiler.compilePush();
		compiler.compileLoadVariable(listVarName);
		compiler.compilePush();
		compiler.compileLoadThis();
		compiler.compileDotCall(Key.get("length"), 1, false);
		compiler.compileOp(OperatorType.LESS);
		end.compileJumpIfNot();
		compiler.compileLoadVariable(listVarName);
		compiler.compilePush();
		compiler.compileLoadVariable(indexVarName);
		compiler.compileIndex();
		compiler.compileStoreVariable(var);
		body.compile(compiler);
		continueLabel.place();
		compiler.compileLoadVariable(indexVarName);
		compiler.compilePush();
		compiler.compileLoadConstant(new FInteger(1));
		compiler.compileOp(OperatorType.PLUS);
		compiler.compileStoreVariable(indexVarName);
		start.compileJump();
		end.place();
		breakLabel.place();

		start.close();
		end.close();
		breakLabel.close();
		continueLabel.close();
	}
}
