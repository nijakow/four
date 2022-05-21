package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.objects.STSymbol;

public class MethodAST {
    private final STSymbol name;
    private final STSymbol[] locals;
    private final BlockAST body;

    public MethodAST(STSymbol name, STSymbol[] locals, BlockAST body) {
        this.name = name;
        this.locals = locals;
        this.body = body;
    }
}
