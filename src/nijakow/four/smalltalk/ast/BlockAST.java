package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.objects.STSymbol;

public class BlockAST extends ExprAST {
    private final STSymbol[] args;
    private final ExprAST body;

    public BlockAST(STSymbol[] args, ExprAST body) {
        this.args = args;
        this.body = body;
    }

    private STCompiledMethod compileThisMethod(STCompiler context) {
        STCompiler compiler = context.subscope();
        for (STSymbol arg : args)
            compiler.addArg(arg);
        body.compile(compiler);
        return compiler.finish(null, null);
    }

    @Override
    public void compile(STCompiler compiler) {
        compiler.writeLoadClosure(compileThisMethod(compiler));
    }

    public ExprAST getBody() {
        return this.body;
    }
}
