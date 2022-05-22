package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;

public abstract class ExprAST extends SmalltalkAST {

    public abstract void compile(STCompiler compiler);
}
