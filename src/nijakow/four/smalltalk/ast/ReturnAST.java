package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;

public class ReturnAST extends ExprAST {
    private final ExprAST value;

    public ReturnAST(ExprAST value) {
        this.value = value;
    }

    @Override
    public void compile(STCompiler compiler) {
        value.compile(compiler);
        compiler.writeReturn();
    }
}
