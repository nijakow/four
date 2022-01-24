package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;

public abstract class AST {

    public void compilationError(String message) throws CompilationException {
        throw new CompilationException(this, message);
    }
}
