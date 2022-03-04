package nijakow.four.share.lang.c.ast;

import nijakow.four.server.runtime.FourClassLoader;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class ASTClassDef extends ASTDecl {
    private final Key name;
    private final ASTClass clazz;

    public ASTClassDef(StreamPosition pos, Key name, ASTClass clazz) {
        super(pos);
        this.name = name;
        this.clazz = clazz;
    }

    @Override
    public void compileInto(Blueprint blueprint, FourClassLoader fs) throws ParseException, CompilationException {
        name.setBlueprint(clazz.compile("<noname>", fs));
    }
}
