package nijakow.four.share.lang.base;

import nijakow.four.share.lang.FourCompilerException;
import nijakow.four.share.lang.c.ast.AST;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;

public class CompilationException extends FourCompilerException {
    private final AST ast;

    public CompilationException(String message) { this(null, message); }
    public CompilationException(AST ast, String message) {
        super(message);
        this.ast = ast;
    }
}
