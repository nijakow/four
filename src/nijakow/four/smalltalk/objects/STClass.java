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
import java.util.function.Supplier;

public class STClass extends STInstance {
    private STClass metaclass;
    private STClass superclass;
    private STSymbol[] members;
    private final Map<STSymbol, STMethod> methods;
    private STInstance name;
    private STInstance category;

    private STClass(STSymbol name, STClass metaclass, STClass superclass, STSymbol[] members) {
        this.name = name;
        this.metaclass = metaclass;
        this.superclass = superclass;
        this.members = members;
        this.methods = new HashMap<>();
        this.category = STNil.get();
    }

    public STClass(STSymbol name, STClass superclass, STSymbol[] members) {
        this(name, null, superclass, members);
    }

    public STClass(STSymbol name) {
        this(name, null, new STSymbol[]{});
    }

    @Override
    public STClass asClass() throws FourException {
        return this;
    }

    public STClass getSuperClass() {
        return this.superclass;
    }

    public void setMetaClass(STClass metaclass) {
        this.metaclass = metaclass;
    }

    public STInstance getCategory() { return this.category; }
    public void setCategory(STInstance category) { this.category = category; }

    public STInstance instantiate() {
        return new STObject(this, this.getInstanceVariableCount());
    }

    public STClass subclass(STSymbol name, World world, STSymbol[] members) {
        STClass subclass = new STClass(name, new STClass(null, getClass(world), new STSymbol[]{}), this, members);
        if (name != null)
            world.setValue(name, subclass);
        return subclass;
    }

    public STClass subclass(STSymbol name, World world) {
        return subclass(name, world, new STSymbol[]{});
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
        STCompiledMethod method = compiler.finish(name, ast.getDocumentar(), source);
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

    public STInstance getMethodAsInstance(STSymbol name) {
        STMethod m = getMethod(name);
        if (m == null) return STNil.get();
        else           return m.asInstance();
    }

    public STMethod findMethod(STSymbol name) {
        STMethod method = methods.get(name);
        if (method == null)
            return (this.superclass == null) ? null : this.superclass.findMethod(name);
        return method;
    }

    public STMethod findSuperMethod(STSymbol name) {
        final STClass superclass = getSuperClass();
        return (superclass == null) ? null : superclass.findMethod(name);
    }

    public STSymbol[] getSelectors() {
        return methods.keySet().toArray(new STSymbol[0]);
    }

    @Override
    public STClass getClass(World world) {
        if (this.metaclass != null)
            return this.metaclass;
        return world.getMetaClass();
    }

    public boolean isSubclassOf(STClass clazz) {
        return (this == clazz) || (getSuperClass() != null && getSuperClass().isSubclassOf(clazz));
    }
}
