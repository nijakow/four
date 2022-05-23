package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;
import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;

public class CommandLineAST {
    private final STSymbol[] locals;
    private final ExprAST expr;

    public CommandLineAST(STSymbol[] locals, ExprAST expr) {
        this.locals = locals;
        this.expr = expr;
    }

    public STCompiledMethod compile(String source) {
        STCompiler compiler = new STCompiler(null, null);
        for (STSymbol local : locals)
            compiler.addLocal(local);
        expr.compile(compiler);
        return compiler.finish(null, source);
    }
}
