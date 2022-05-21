package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.objects.STSymbol;

public class BlockAST {
    private final STSymbol[] args;
    private final ExprAST body;

    public BlockAST(STSymbol[] args, ExprAST body) {
        this.args = args;
        this.body = body;
    }
}
