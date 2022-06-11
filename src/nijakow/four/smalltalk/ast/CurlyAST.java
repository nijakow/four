package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;

public class CurlyAST extends ExprAST {
    private final ExprAST[] elements;

    public CurlyAST(ExprAST[] elements) {
        this.elements = elements;
    }

    @Override
    public void compile(STCompiler compiler) {
        for (ExprAST expr : this.elements) {
            expr.compile(compiler);
            compiler.writePush();
        }
        compiler.writeMakeArray(elements.length);
    }
}
