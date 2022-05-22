package nijakow.four.smalltalk;

import nijakow.four.smalltalk.objects.STClass;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STSymbol;

import java.util.HashMap;
import java.util.Map;

public class World {
    private final Map<STSymbol, STInstance> bindings = new HashMap<>();

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
        STClass objectClass = new STClass();
        setValue(STSymbol.get("Object"), objectClass);

        objectClass.addMethod("sayHi", (fiber, args) -> {
            System.out.println("Hi! :D");
        });
    }
}
