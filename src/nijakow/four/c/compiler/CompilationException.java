package nijakow.four.c.compiler;

import nijakow.four.c.ast.AST;
import nijakow.four.runtime.FourRuntimeException;

public class CompilationException extends FourRuntimeException {
    private final AST ast;


    public CompilationException(String message) { this(null, message); }
    public CompilationException(AST ast, String message) {
        super(message);
        this.ast = ast;
    }
}
