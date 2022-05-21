package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.objects.STSymbol;

public class SendAST extends ExprAST {
    private final ExprAST receiver;
    private final STSymbol message;
    private final ExprAST[] arguments;

    public SendAST(ExprAST receiver, STSymbol message, ExprAST[] arguments) {
        this.receiver = receiver;
        this.message = message;
        this.arguments = arguments;
    }
}
