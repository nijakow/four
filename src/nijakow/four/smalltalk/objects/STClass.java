package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.ast.MethodAST;
import nijakow.four.smalltalk.compiler.STCompiler;
import nijakow.four.smalltalk.objects.method.STBuiltinMethod;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.parser.ParseException;
import nijakow.four.smalltalk.parser.Parser;
import nijakow.four.smalltalk.parser.StringCharacterStream;
import nijakow.four.smalltalk.parser.Tokenizer;
import nijakow.four.smalltalk.vm.Builtin;
import nijakow.four.smalltalk.vm.FourException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class STClass extends STInstance {
    private final STClass superclass;
    private STSymbol[] members;
    private final Map<STSymbol, STMethod> methods;
    private Supplier<STInstance> instantiator;
    private Function<STInstance, STInstance> instantiator2;
    private String category;

    private STClass(STClass superclass, STSymbol[] members) {
        this.superclass = superclass;
        this.members = members;
        this.methods = new HashMap<>();
        this.instantiator = () -> new STObject(this, getInstanceVariableCount());
        this.instantiator2 = (arg) -> STNil.get();
    }

    public STClass() {
        this(null, new STSymbol[]{});
    }

    @Override
    public STClass asClass() throws FourException {
        return this;
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

    private int getParentInstanceVariableCount() {
        return (superclass == null) ? 0 : superclass.getInstanceVariableCount();
    }

    public int getInstanceVariableCount() {
        return this.members.length + getParentInstanceVariableCount();
    }

    public int getInstanceVariableIndex(STSymbol name) {
        int i = (superclass == null) ? -1 : superclass.getInstanceVariableIndex(name);
        for (int x = 0; x < this.members.length; x++)
            if (this.members[x] == name)
                return x + getParentInstanceVariableCount();
        return i;
    }

    public STSymbol[] getInstanceVariableNames() {
        STSymbol[] names = new STSymbol[this.members.length];
        for (int i = 0; i < names.length; i++)
            names[i] = this.members[i];
        return names;
    }

    public void setInstanceVariableNames(String value) {
        String[] varnames = value.split("\\s+");
        STSymbol[] vars = new STSymbol[varnames.length];
        for (int i = 0; i < vars.length; i++)
            vars[i] = STSymbol.get(varnames[i]);
        this.members = vars;
        /*
         * TODO: Update all instances
         */
    }

    public String getCategory() { return this.category; }
    public void setCategory(String category) { this.category = category; }


    public STCompiler openCompiler() {
        return new STCompiler(this, null);
    }

    public void addMethodFromSource(String source) throws ParseException {
        StringCharacterStream cs = new StringCharacterStream(source);
        Tokenizer tokenizer = new Tokenizer(cs);
        Parser parser = new Parser(tokenizer);
        MethodAST ast = parser.parseMethod();
        STSymbol name = ast.getName();
        STCompiler compiler = openCompiler();
        ast.compile(compiler);
        STCompiledMethod method = compiler.finish(name, source);
        methods.put(name, method);
    }

    public void addMethod(STSymbol name, Builtin method) {
        methods.put(name, new STBuiltinMethod(method));
    }

    public void addMethod(String name, Builtin method) {
        addMethod(STSymbol.get(name), method);
    }

    public void removeMethod(STSymbol name) {
        methods.remove(name);
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

    public STMethod findSuperMethod(STSymbol name) {
        final STClass superclass = getSuperClass();
        return (superclass == null) ? null : superclass.getMethod(name);
    }

    public STSymbol[] getSelectors() {
        return methods.keySet().toArray(new STSymbol[0]);
    }

    @Override
    public STClass getClass(World world) {
        return world.getMetaClass();
    }

    public boolean isSubclassOf(STClass clazz) {
        return (this == clazz) || (getSuperClass() != null && getSuperClass().isSubclassOf(clazz));
    }
}
