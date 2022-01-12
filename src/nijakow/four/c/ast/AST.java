package nijakow.four.c.ast;

import nijakow.four.c.compiler.CompilationException;

public abstract class AST {

    public void compilationError(String message) throws CompilationException {
        throw new CompilationException(this, message);
    }
}
