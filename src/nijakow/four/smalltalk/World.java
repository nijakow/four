package nijakow.four.smalltalk;

import nijakow.four.smalltalk.objects.STClass;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STObject;
import nijakow.four.smalltalk.objects.STSymbol;

import java.util.HashMap;
import java.util.Map;

public class World {
    private final Map<STSymbol, STInstance> bindings = new HashMap<>();

    private STClass objectClass;
    private STClass metaClass;
    private STClass integerClass;
    private STClass stringClass;
    private STClass symbolClass;
    private STClass closureClass;
    private STClass compiledMethodClass;
    private STClass builtinMethodClass;

    public World() {
    }

    public STInstance getValue(STSymbol symbol) {
        return bindings.get(symbol);
    }

    public void setValue(String name, STInstance value) {
        setValue(STSymbol.get(name), value);
    }

    public void setValue(STSymbol symbol, STInstance value) {
        bindings.put(symbol, value);
    }

    public STClass getMetaClass() {
        return this.metaClass;
    }

    public STClass getIntegerClass() {
        return this.integerClass;
    }

    public STClass getStringClass() {
        return this.stringClass;
    }

    public STClass getSymbolClass() {
        return this.symbolClass;
    }

    public STClass getClosureClass() {
        return this.closureClass;
    }

    public STClass getCompiledMethodClass() {
        return this.compiledMethodClass;
    }

    public STClass getBuiltinMethodClass() {
        return this.builtinMethodClass;
    }

    public void buildDefaultWorld() {
        objectClass = new STClass();
        setValue("Object", objectClass);

        objectClass.addMethod("sayHi", (fiber, args) -> {
            System.out.println("Hi! :D");
        });

        metaClass = objectClass.subclass();
        metaClass.addMethod("subclass:", (fiber, args) -> {
            final STSymbol name = ((STSymbol) args[1]);
            STClass clazz = ((STClass) args[0]).subclass();
            setValue(name, clazz);
        });

        STClass fourClass = objectClass.subclass();
        fourClass.addMethodFromSource("run\n[\n    self sayHi.\n]\n");

        STObject four = fourClass.instantiate();
        setValue("Four", four);
    }
}
