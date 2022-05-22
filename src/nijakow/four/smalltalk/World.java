package nijakow.four.smalltalk;

import nijakow.four.smalltalk.objects.*;
import nijakow.four.smalltalk.objects.method.STBuiltinMethod;
import nijakow.four.smalltalk.vm.Fiber;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class World {
    private final Map<STSymbol, STInstance> bindings = new HashMap<>();

    private STClass objectClass;
    private STClass metaClass;
    private STClass integerClass;
    private STClass stringClass;
    private STClass symbolClass;
    private STClass closureClass;
    private STClass methodClass;
    private STClass compiledMethodClass;
    private STClass builtinMethodClass;
    private STClass portClass;

    public World() {
    }

    public STInstance getValue(String name) {
        return getValue(STSymbol.get(name));
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

    public STClass getPortClass() {
        return this.portClass;
    }

    public void buildDefaultWorld() {
        objectClass = new STClass();
        setValue("Object", objectClass);

        objectClass.addMethod("sayHi", (fiber, args) -> {
            System.out.println("Hi! :D");
        });

        metaClass = new STClass();
        metaClass.addMethod("subclass:", (fiber, args) -> {
            final STSymbol name = ((STSymbol) args[1]);
            STClass clazz = ((STClass) args[0]).subclass();
            setValue(name, clazz);
        });

        integerClass = objectClass.subclass();
        stringClass = objectClass.subclass();
        symbolClass = objectClass.subclass();
        closureClass = objectClass.subclass();
        methodClass = objectClass.subclass();
        compiledMethodClass = methodClass.subclass();
        builtinMethodClass = methodClass.subclass();
        portClass = objectClass.subclass();

        BiConsumer<Fiber, STInstance[]> valueBuiltin = (fiber, args) -> {
            fiber.loadSelf();
            fiber.push();
            for (int x = 1; x < args.length; x++)
                fiber.push(args[x]);
            ((STClosure) args[0]).execute(fiber, args.length - 1);
        };

        closureClass.addMethod("value", valueBuiltin);
        closureClass.addMethod("value:", valueBuiltin);
        closureClass.addMethod("value:value:", valueBuiltin);
        closureClass.addMethod("value:value:value:", valueBuiltin);
        closureClass.addMethod("value:value:value:value:", valueBuiltin);
        closureClass.addMethod("value:value:value:value:value:", valueBuiltin);
        closureClass.addMethod("value:value:value:value:value:value:", valueBuiltin);

        portClass.addMethod("out:", (fiber, args) -> {
            ((STPort) args[0]).write(((STString) args[1]).getValue());
        });
        portClass.addMethod("close", (fiber, args) -> {
            ((STPort) args[0]).close();
        });

        STClass fourClass = objectClass.subclass();
        fourClass.addMethodFromSource("run\n[\n]\n");
        fourClass.addMethodFromSource("newConnection: connection\n[\n    connection out: 'Hi!'; close.\n]\n");

        STObject four = fourClass.instantiate();
        setValue("Four", four);
    }
}
