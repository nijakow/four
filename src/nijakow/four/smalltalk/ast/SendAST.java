package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;
import nijakow.four.smalltalk.compiler.STCompilerLabel;
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
        if (this.message == STSymbol.get("repeat")) {
            STCompilerLabel top = compiler.openLabel();
            top.place();
            ((BlockAST) this.receiver).getBody().compile(compiler);
            top.jump();
        } else if (this.message == STSymbol.get("ifTrue:")) {
            STCompilerLabel end = compiler.openLabel();
            this.receiver.compile(compiler);
            end.jumpIfNot();
            ((BlockAST) this.arguments[0]).getBody().compile(compiler);
            end.place();
        } else if (this.message == STSymbol.get("ifFalse:")) {
            STCompilerLabel end = compiler.openLabel();
            STCompilerLabel code = compiler.openLabel();
            this.receiver.compile(compiler);
            code.jumpIfNot();
            end.jump();
            code.place();
            ((BlockAST) this.arguments[0]).getBody().compile(compiler);
            end.place();
        } else if (this.message == STSymbol.get("ifTrue:ifFalse:")) {
            STCompilerLabel end = compiler.openLabel();
            STCompilerLabel code = compiler.openLabel();
            this.receiver.compile(compiler);
            code.jumpIfNot();
            ((BlockAST) this.arguments[0]).getBody().compile(compiler);
            end.jump();
            code.place();
            ((BlockAST) this.arguments[1]).getBody().compile(compiler);
            end.place();
        } else {
            receiver.compile(compiler);
            compiler.writePush();
            for (ExprAST arg : arguments) {
                arg.compile(compiler);
                compiler.writePush();
            }
            compiler.writeSend(message, arguments.length);
        }
    }
}
