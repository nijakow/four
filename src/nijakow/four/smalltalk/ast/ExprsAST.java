package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;

public class ExprsAST extends ExprAST {
    private final ExprAST[] expressions;

    public ExprsAST(ExprAST[] expressions) {
        this.expressions = expressions;
    }

    @Override
    public void compile(STCompiler compiler) {
        for (ExprAST expr : expressions)
            expr.compile(compiler);
    }
}
