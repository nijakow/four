package nijakow.four.lang.c.compiler;

import nijakow.four.lang.c.ast.AST;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;

public class CompilationException extends FourRuntimeException {
    private final AST ast;


    public CompilationException(String message) { this(null, message); }
    public CompilationException(AST ast, String message) {
        super(message);
        this.ast = ast;
    }
}
