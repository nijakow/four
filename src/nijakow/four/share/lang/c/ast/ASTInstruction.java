package nijakow.four.share.lang.c.ast;

import nijakow.four.server.compiler.ScopedCompiler;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.server.runtime.vm.code.CodeMeta;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.parser.StreamPosition;

public abstract class ASTInstruction extends AST {

	public ASTInstruction(StreamPosition pos) {
		super(pos);
	}

	void compile(FCompiler compiler) throws CompilationException {
		compilationError("Can not compile this expression!");
	}

	public Code compileStandalone() throws CompilationException {
		ScopedCompiler compiler = new ScopedCompiler(new CodeMeta(getPos(), "This code was compiled as a standalone directly from an AST.", Type.getAny(), new Type[0]));
		compiler.tell(this);
		compile(compiler);
		compiler.compileReturn();
		return compiler.finish();
	}
}
