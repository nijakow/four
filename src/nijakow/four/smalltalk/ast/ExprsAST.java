package nijakow.four.smalltalk.ast;

public class ExprsAST extends ExprAST {
    private final ExprAST[] expressions;

    public ExprsAST(ExprAST[] expressions) {
        this.expressions = expressions;
    }
}
