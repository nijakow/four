package nijakow.four.lang.c.ast;

import nijakow.four.lang.c.compiler.CompilationException;

public abstract class AST {

    public void compilationError(String message) throws CompilationException {
        throw new CompilationException(this, message);
    }
}
