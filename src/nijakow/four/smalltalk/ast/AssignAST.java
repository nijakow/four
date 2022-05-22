package nijakow.four.smalltalk.ast;

import nijakow.four.smalltalk.compiler.STCompiler;
import nijakow.four.smalltalk.objects.STSymbol;

public class AssignAST extends ExprAST {
    private final STSymbol lhs;
    private final ExprAST rhs;

    public AssignAST(STSymbol lhs, ExprAST rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    @Override
    public void compile(STCompiler compiler) {
        rhs.compile(compiler);
        compiler.writeStore(lhs);
    }
}
