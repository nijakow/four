package nijakow.four.share.lang.c.ast;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.StreamPosition;

public abstract class AST {
    private final StreamPosition pos;

    public AST(StreamPosition pos) {
        this.pos = pos;
    }

    public StreamPosition getPos() {
        return this.pos;
    }

    public void compilationError(String message) throws CompilationException {
        throw new CompilationException(this, message);
    }
}
