package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.ast.MethodAST;
import nijakow.four.smalltalk.compiler.STCompiler;
import nijakow.four.smalltalk.objects.method.STBuiltinMethod;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.parser.Parser;
import nijakow.four.smalltalk.parser.StringCharacterStream;
import nijakow.four.smalltalk.parser.Tokenizer;
import nijakow.four.smalltalk.vm.Fiber;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class STClass extends STInstance {
    private final STClass superclass;
    private final STSymbol[] members;
    private final Map<STSymbol, STMethod> methods;
    private Supplier<STInstance> instantiator;
    private Function<STInstance, STInstance> instantiator2;

    private STClass(STClass superclass, STSymbol[] members) {
        this.superclass = superclass;
        this.members = members;
        this.methods = new HashMap<>();
        this.instantiator = () -> new STObject(this, this.members.length);
        this.instantiator2 = (arg) -> STNil.get();
    }

    public STClass() {
        this(null, new STSymbol[]{});
    }

    public STClass getSuperClass() {
        return this.superclass;
    }

    public STInstance instantiate() {
        return instantiator.get();
    }

    public void setInstantiator(Supplier<STInstance> instantiator) {
        this.instantiator = instantiator;
    }

    public STInstance instantiate(STInstance argument) {
        return instantiator2.apply(argument);
    }

    public void setInstantiator2(Function<STInstance, STInstance> instantiator) {
        this.instantiator2 = instantiator;
    }

    public STClass subclass(STSymbol[] members) {
        return new STClass(this, members);
    }

    public STClass subclass() {
        return new STClass(this, new STSymbol[]{});
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
        STCompiledMethod method = compiler.finish(source);
        methods.put(name, method);
    }

    public void addMethod(STSymbol name, BiConsumer<Fiber, STInstance[]> method) {
        methods.put(name, new STBuiltinMethod(method));
    }

    public void addMethod(String name, BiConsumer<Fiber, STInstance[]> method) {
        addMethod(STSymbol.get(name), method);
    }

    public STMethod getMethod(STSymbol name) {
        return methods.get(name);
    }

    public STMethod findMethod(STSymbol name) {
        STMethod method = methods.get(name);
        if (method == null)
            return (this.superclass == null) ? null : this.superclass.findMethod(name);
        return method;
    }

    public int findMember(STSymbol name) {
        for (int i = 0; i < members.length; i++)
            if (members[i] == name)
                return i;
        return -1;
    }

    @Override
    public STClass getClass(World world) {
        return world.getMetaClass();
    }
}
