package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;
import nijakow.four.smalltalk.objects.STSymbol;

public class MethodAST {
    private final STSymbol name;
    private final STSymbol[] args;
    private final STSymbol[] locals;
    private final ExprAST body;

    public MethodAST(STSymbol name, STSymbol[] args, STSymbol[] locals, ExprAST body) {
        this.args = args;
        this.name = name;
        this.locals = locals;
        this.body = body;
    }

    public STSymbol getName() {
        return this.name;
    }

    public void compile(STCompiler compiler) {
        for (STSymbol arg : args)
            compiler.addArg(arg);
        for (STSymbol local : locals)
            compiler.addLocal(local);
        body.compile(compiler);
    }
}
