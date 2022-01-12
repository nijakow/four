package nijakow.four.c.compiler;

import nijakow.four.FourException;
import nijakow.four.c.ast.AST;

public class CompilationException extends FourException {
    private final AST ast;


    public CompilationException(String message) { this(null, message); }
    public CompilationException(AST ast, String message) {
        super(message);
        this.ast = ast;
    }
}
