package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;

public class SuperAST extends ExprAST {
    @Override
    public void compile(STCompiler compiler) {
        compiler.writeLoadSelf();
    }
}
