package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.objects.STSymbol;

public class SymbolAST extends ExprAST {
    private final STSymbol symbol;

    public SymbolAST(STSymbol symbol) {
        this.symbol = symbol;
    }
}
