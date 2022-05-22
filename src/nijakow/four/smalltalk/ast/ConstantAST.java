package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;
import nijakow.four.smalltalk.objects.STInstance;

public class ConstantAST extends ExprAST {
    private final STInstance value;

    public ConstantAST(STInstance value) {
        this.value = value;
    }

    @Override
    public void compile(STCompiler compiler) {
        compiler.writeLoadConstant(this.value);
    }
}
