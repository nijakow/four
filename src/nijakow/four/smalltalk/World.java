package nijakow.four.smalltalk;

import nijakow.four.server.runtime.objects.blue.Method;
import nijakow.four.smalltalk.objects.*;
import nijakow.four.smalltalk.objects.method.STBuiltinMethod;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.vm.Fiber;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class World {
    private final Map<STSymbol, STInstance> bindings = new HashMap<>();

    private STClass objectClass;
    private STClass metaClass;
    private STClass nilClass;
    private STClass booleanClass;
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

    public STClass getNilClass() {
        return this.nilClass;
    }

    public STClass getBooleanClass() {
        return this.booleanClass;
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
        objectClass.addMethod("value", (fiber, args) -> fiber.setAccu(args[0]));
        objectClass.addMethod("class", (fiber, args) -> fiber.setAccu(args[0].getClass(fiber.getVM().getWorld())));
        objectClass.addMethod("asBool", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].isTrue())));
        objectClass.addMethod("=", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0] == args[1])));
        objectClass.addMethodFromSource("init\n[\n]\n");

        metaClass = new STClass();
        setValue("Class", metaClass);
        metaClass.addMethod("new", (fiber, args) -> {
            fiber.enter(((STClass) args[0]).instantiate(), "init", new STInstance[]{});
        });
        metaClass.addMethod("subclass:", (fiber, args) -> {
            final STSymbol name = ((STSymbol) args[1]);
            STClass clazz = ((STClass) args[0]).subclass();
            setValue(name, clazz);
        });
        metaClass.addMethod("method:", (fiber, args) -> {
            STMethod method = ((STClass) args[0]).getMethod((STSymbol) args[1]);
            fiber.setAccu(method.asInstance());
        });

        nilClass = new STClass();
        booleanClass = objectClass.subclass();
        integerClass = objectClass.subclass();
        stringClass = objectClass.subclass();
        symbolClass = objectClass.subclass();
        closureClass = objectClass.subclass();
        methodClass = objectClass.subclass();
        compiledMethodClass = methodClass.subclass();
        builtinMethodClass = methodClass.subclass();
        portClass = objectClass.subclass();

        integerClass.addMethod("+", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() + ((STInteger) args[1]).getValue())));
        integerClass.addMethod("-", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() - ((STInteger) args[1]).getValue())));
        integerClass.addMethod("*", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() * ((STInteger) args[1]).getValue())));
        integerClass.addMethod("/", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() / ((STInteger) args[1]).getValue())));
        integerClass.addMethod("mod:", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() % ((STInteger) args[1]).getValue())));
        integerClass.addMethod("<", (fiber, args) -> fiber.setAccu(STBoolean.get(((STInteger) args[0]).getValue() < ((STInteger) args[1]).getValue())));
        integerClass.addMethod("<=", (fiber, args) -> fiber.setAccu(STBoolean.get(((STInteger) args[0]).getValue() <= ((STInteger) args[1]).getValue())));
        integerClass.addMethod(">", (fiber, args) -> fiber.setAccu(STBoolean.get(((STInteger) args[0]).getValue() > ((STInteger) args[1]).getValue())));
        integerClass.addMethod(">=", (fiber, args) -> fiber.setAccu(STBoolean.get(((STInteger) args[0]).getValue() >= ((STInteger) args[1]).getValue())));

        stringClass.addMethod("size", (fiber, args) -> fiber.setAccu(STInteger.get((((STString) args[0]).getValue().length()))));
        stringClass.addMethod("from:to:", (fiber, args) -> fiber.setAccu(new STString((((STString) args[0]).getValue().substring(((STInteger) args[1]).getValue(), ((STInteger) args[2]).getValue())))));
        stringClass.addMethod("+", (fiber, args) -> fiber.setAccu(new STString(((STString) args[0]).getValue() + ((STString) args[1]).getValue())));
        stringClass.addMethod("compile", (fiber, args) -> fiber.setAccu(new STClosure(((STString) args[0]).compile(), null)));
        stringClass.addMethod("asSymbol", (fiber, args) -> fiber.setAccu(STSymbol.get(((STString) args[0]).getValue())));

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
        closureClass.addMethod("value:value:value:value:value:value:value:", valueBuiltin);

        compiledMethodClass.addMethod("source", (fiber, args) -> fiber.setAccu(new STString(((STCompiledMethod) args[0]).getSource())));

        portClass.addMethod("prompt:", (fiber, args) -> {
            fiber.pause();
            ((STPort) args[0]).prompt(((STString) args[1]).getValue());
            ((STPort) args[0]).onInput((STString line) -> fiber.restartWithValue(line));
        });
        portClass.addMethod("password:", (fiber, args) -> {
            fiber.pause();
            ((STPort) args[0]).password(((STString) args[1]).getValue());
            ((STPort) args[0]).onInput((STString line) -> fiber.restartWithValue(line));
        });
        portClass.addMethod("out:", (fiber, args) -> {
            ((STPort) args[0]).write(((STString) args[1]).getValue());
        });
        portClass.addMethod("edit:title:", (fiber, args) -> {
            fiber.pause();
            ((STPort) args[0]).edit(((STString) args[2]).getValue(), ((STString) args[1]).getValue(), (v) -> {
                if (v == null) fiber.restartWithValue(STNil.get());
                else fiber.restartWithValue(v);
            });
        });
        portClass.addMethod("close", (fiber, args) -> {
            ((STPort) args[0]).close();
        });

        STClass fourClass = objectClass.subclass();
        fourClass.addMethodFromSource("run\n[\n]\n");
        fourClass.addMethodFromSource("newConnection: connection\n[\n    connection out: (connection edit: ((Four class method: #'newConnection:') source) title: 'Four>>newConnection:').\n]\n");

        STObject four = fourClass.instantiate();
        setValue("Four", four);
    }
}
