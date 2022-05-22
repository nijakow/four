package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;
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

    @Override
    public void compile(STCompiler compiler) {
        for (ExprAST arg : arguments) {
            arg.compile(compiler);
            compiler.writePush();
        }
        compiler.writeSend(message, arguments.length);
    }
}
