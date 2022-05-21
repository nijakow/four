package nijakow.four.share.lang.c.ast;

import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.base.FCompiler;
import nijakow.four.share.lang.base.parser.StreamPosition;

public class ASTCast extends ASTExpression {
    private final Type type;
    private final ASTExpression expr;

    public ASTCast(StreamPosition pos, Type type, ASTExpression expr) {
        super(pos);
        this.type = type;
        this.expr = expr;
    }

    @Override
    void compile(FCompiler compiler) throws CompilationException {
        expr.compile(compiler);
        compiler.tell(this);
        compiler.compileCast(type);
    }
}
