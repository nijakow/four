package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.ast.MethodAST;
import nijakow.four.smalltalk.compiler.STCompiler;
import nijakow.four.smalltalk.parser.Parser;
import nijakow.four.smalltalk.parser.StringCharacterStream;
import nijakow.four.smalltalk.parser.Tokenizer;

import java.util.HashMap;
import java.util.Map;

public class STClass extends STInstance {
    private final STSymbol[] members;
    private final Map<STSymbol, STMethod> methods;

    public STClass(STSymbol[] members) {
        this.members = members;
        this.methods = new HashMap<>();
    }

    public STClass() {
        this(new STSymbol[]{});
    }

    public STObject instantiate() {
        return new STObject(members.length);
    }

    public STCompiler openCompiler() {
        return new STCompiler(this, null);
    }

    public void addMethodFromSource(String source) {
        StringCharacterStream cs = new StringCharacterStream(source);
        Tokenizer tokenizer = new Tokenizer(cs);
        Parser parser = new Parser(tokenizer);
        MethodAST ast = parser.parseMethod();
        STSymbol name = ast.getName();
        STCompiler compiler = openCompiler();
        ast.compile(compiler);
        STMethod method = compiler.finish(source);
        methods.put(name, method);
    }

    public STMethod getMethod(STSymbol name) {
        return methods.get(name);
    }

    public int findMember(STSymbol name) {
        for (int i = 0; i < members.length; i++)
            if (members[i] == name)
                return i;
        return -1;
    }
}
