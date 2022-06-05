package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;
import nijakow.four.smalltalk.objects.STSymbol;

public class BuiltinAST extends ExprAST {
    private final STSymbol builtin;

    public BuiltinAST(STSymbol builtin) {
        this.builtin = builtin;
    }

    @Override
    public void compile(STCompiler compiler) {
        compiler.writeBuiltin(builtin);
    }
}
