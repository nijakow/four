package nijakow.four.smalltalk;

import nijakow.four.server.runtime.objects.blue.Method;
import nijakow.four.smalltalk.objects.*;
import nijakow.four.smalltalk.objects.method.STBuiltinMethod;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.vm.Builtin;
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
    private STClass characterClass;
    private STClass stringClass;
    private STClass symbolClass;
    private STClass arrayClass;
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
        return bindings.getOrDefault(symbol, STNil.get());
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

    public STClass getCharacterClass() {
        return this.characterClass;
    }

    public STClass getStringClass() {
        return this.stringClass;
    }

    public STClass getSymbolClass() {
        return this.symbolClass;
    }

    public STClass getArrayClass() {
        return this.arrayClass;
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
        objectClass.addMethodFromSource("init\n[\n]\n");
        objectClass.addMethod("value", (fiber, args) -> fiber.setAccu(args[0]));
        objectClass.addMethod("class", (fiber, args) -> fiber.setAccu(args[0].getClass(fiber.getVM().getWorld())));
        objectClass.addMethod("asBool", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].isTrue())));
        objectClass.addMethod("toString", (fiber, args) -> fiber.setAccu(new STString(args[0].toString())));
        objectClass.addMethod("=", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].is(args[1]))));
        objectClass.addMethodFromSource("writeOn: w\n[\n    w out: self toString\n]\n");
        objectClass.addMethodFromSource("storeOn: w\n[\n    w out: self toString\n]\n");

        metaClass = objectClass.subclass();
        setValue("Class", metaClass);
        metaClass.addMethod("new", (fiber, args) -> {
            fiber.enter(((STClass) args[0]).instantiate(), "init", new STInstance[]{});
        });
        metaClass.addMethod("new:", (fiber, args) -> {
            fiber.enter(((STClass) args[0]).instantiate(args[1]), "init", new STInstance[]{});
        });
        metaClass.addMethod("parent", (fiber, args) -> {
            STClass parent = ((STClass) args[0]).getSuperClass();
            if (parent == null)
                fiber.setAccu(STNil.get());
            else
                fiber.setAccu(parent);
        });
        metaClass.addMethod("subclass:", (fiber, args) -> {
            final STSymbol name = ((STSymbol) args[1]);
            STClass clazz = ((STClass) args[0]).subclass();
            setValue(name, clazz);
        });
        metaClass.addMethod("method:", (fiber, args) -> {
            STMethod method = ((STClass) args[0]).getMethod((STSymbol) args[1]);
            if (method == null || method.asInstance() == null)
                fiber.setAccu(STNil.get());
            else
                fiber.setAccu(method.asInstance());
        });
        metaClass.addMethod("addMethod:", (fiber, args) -> {
            ((STClass) args[0]).addMethodFromSource(((STString) args[1]).getValue());
        });
        metaClass.addMethod("selectors", (fiber, args) -> {
            STSymbol[] selectors = ((STClass) args[0]).getSelectors();
            STArray result = new STArray(selectors);
            fiber.setAccu(result);
        });

        nilClass = objectClass.subclass();
        booleanClass = objectClass.subclass();
        integerClass = objectClass.subclass();
        characterClass = objectClass.subclass();
        stringClass = objectClass.subclass();
        symbolClass = objectClass.subclass();
        arrayClass = objectClass.subclass();
        closureClass = objectClass.subclass();
        methodClass = objectClass.subclass();
        compiledMethodClass = methodClass.subclass();
        builtinMethodClass = methodClass.subclass();
        portClass = objectClass.subclass();

        setValue("Nil", nilClass);

        setValue("Boolean", booleanClass);
        booleanClass.addMethod("not", (fiber, args) -> fiber.setAccu(STBoolean.get(!args[0].isTrue())));
        booleanClass.addMethod("&", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].isTrue() && args[1].isTrue())));
        booleanClass.addMethod("|", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].isTrue() || args[1].isTrue())));

        setValue("Integer", integerClass);
        integerClass.addMethod("asChar", (fiber, args) -> fiber.setAccu(STCharacter.get((char) ((STInteger) args[0]).getValue())));
        integerClass.addMethod("+", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() + ((STInteger) args[1]).getValue())));
        integerClass.addMethod("-", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() - ((STInteger) args[1]).getValue())));
        integerClass.addMethod("*", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() * ((STInteger) args[1]).getValue())));
        integerClass.addMethod("/", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() / ((STInteger) args[1]).getValue())));
        integerClass.addMethod("mod:", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() % ((STInteger) args[1]).getValue())));
        integerClass.addMethod("negate", (fiber, args) -> fiber.setAccu(STInteger.get(-((STInteger) args[0]).getValue())));
        integerClass.addMethod("bitAnd:", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() & ((STInteger) args[1]).getValue())));
        integerClass.addMethod("bitOr:", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() | ((STInteger) args[1]).getValue())));
        integerClass.addMethod("bitXor:", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() ^ ((STInteger) args[1]).getValue())));
        integerClass.addMethod("leftShift:", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() << ((STInteger) args[1]).getValue())));
        integerClass.addMethod("rightShift:", (fiber, args) -> fiber.setAccu(STInteger.get(((STInteger) args[0]).getValue() >> ((STInteger) args[1]).getValue())));
        integerClass.addMethod("bitNot", (fiber, args) -> fiber.setAccu(STInteger.get(~((STInteger) args[0]).getValue())));
        integerClass.addMethod("<", (fiber, args) -> fiber.setAccu(STBoolean.get(((STInteger) args[0]).getValue() < ((STInteger) args[1]).getValue())));
        integerClass.addMethod("<=", (fiber, args) -> fiber.setAccu(STBoolean.get(((STInteger) args[0]).getValue() <= ((STInteger) args[1]).getValue())));
        integerClass.addMethod(">", (fiber, args) -> fiber.setAccu(STBoolean.get(((STInteger) args[0]).getValue() > ((STInteger) args[1]).getValue())));
        integerClass.addMethod(">=", (fiber, args) -> fiber.setAccu(STBoolean.get(((STInteger) args[0]).getValue() >= ((STInteger) args[1]).getValue())));
        integerClass.addMethodFromSource("to: upper do: block | v\n[\n    v := self.\n    [ v <= upper ] whileTrue: [\n        block value: v.\n        v := v + 1.\n    ].\n  ^ self\n]\n");

        setValue("Character", characterClass);
        characterClass.addMethod("asInt", (fiber, args) -> fiber.setAccu(STInteger.get(((STCharacter) args[0]).getValue())));
        characterClass.addMethod("isWhitespace", (fiber, args) -> fiber.setAccu(STBoolean.get(Character.isWhitespace(((STCharacter) args[0]).getValue()))));
        characterClass.addMethod("<", (fiber, args) -> fiber.setAccu(STBoolean.get(((STCharacter) args[0]).getValue() < ((STCharacter) args[1]).getValue())));
        characterClass.addMethod("<=", (fiber, args) -> fiber.setAccu(STBoolean.get(((STCharacter) args[0]).getValue() <= ((STCharacter) args[1]).getValue())));
        characterClass.addMethod(">", (fiber, args) -> fiber.setAccu(STBoolean.get(((STCharacter) args[0]).getValue() > ((STCharacter) args[1]).getValue())));
        characterClass.addMethod(">=", (fiber, args) -> fiber.setAccu(STBoolean.get(((STCharacter) args[0]).getValue() >= ((STCharacter) args[1]).getValue())));
        characterClass.addMethodFromSource("writeOn: w\n[\n    w outputChar: self\n]\n");

        setValue("String", stringClass);
        stringClass.addMethod("size", (fiber, args) -> fiber.setAccu(STInteger.get((((STString) args[0]).getValue().length()))));
        stringClass.addMethod("at:", (fiber, args) -> fiber.setAccu(STCharacter.get((((STString) args[0]).getValue().charAt(((STInteger) args[1]).getValue())))));
        stringClass.addMethod("from:to:", (fiber, args) -> fiber.setAccu(new STString((((STString) args[0]).getValue().substring(((STInteger) args[1]).getValue(), ((STInteger) args[2]).getValue())))));
        stringClass.addMethod("+", (fiber, args) -> fiber.setAccu(new STString(((STString) args[0]).getValue() + ((STString) args[1]).getValue())));
        stringClass.addMethod("compile", (fiber, args) -> fiber.setAccu(new STClosure(((STString) args[0]).compile(), null)));
        stringClass.addMethod("asSymbol", (fiber, args) -> fiber.setAccu(STSymbol.get(((STString) args[0]).getValue())));
        stringClass.addMethodFromSource("do: block\n[\n    0 to: self size - 1 do: [ :i | block value: (self at: i) ].\n  ^ self\n]\n");
        stringClass.addMethodFromSource("writeOn: w\n[\n    self do: [ :c | w out: c ]\n]\n");

        setValue("Symbol", symbolClass);
        symbolClass.addMethod("asString", (fiber, args) -> fiber.setAccu(new STString(((STSymbol) args[0]).getName())));

        setValue("Array", arrayClass);
        arrayClass.setInstantiator(() -> new STArray(0));
        arrayClass.setInstantiator2((size) -> new STArray(((STInteger) size).getValue()));
        arrayClass.addMethod("size", (fiber, args) -> fiber.setAccu(STInteger.get(((STArray) args[0]).getSize())));
        arrayClass.addMethod("at:", (fiber, args) -> fiber.setAccu(((STArray) args[0]).get(((STInteger) args[1]).getValue())));
        arrayClass.addMethod("at:put:", (fiber, args) -> ((STArray) args[0]).set(((STInteger) args[1]).getValue(), args[2]));
        arrayClass.addMethodFromSource("do: block\n[\n    0 to: self size - 1 do: [ :i | block value: (self at: i) ].\n  ^ self\n]\n");

        setValue("Method", methodClass);
        Builtin valueBuiltin = (fiber, args) -> {
            fiber.loadLexicalSelf(((STClosure) args[0]).getContext());
            fiber.push();
            for (int x = 1; x < args.length; x++)
                fiber.push(args[x]);
            ((STClosure) args[0]).execute(fiber, args.length - 1);
        };

        setValue("BlockClosure", closureClass);
        closureClass.addMethod("value", valueBuiltin);
        closureClass.addMethod("value:", valueBuiltin);
        closureClass.addMethod("value:value:", valueBuiltin);
        closureClass.addMethod("value:value:value:", valueBuiltin);
        closureClass.addMethod("value:value:value:value:", valueBuiltin);
        closureClass.addMethod("value:value:value:value:value:", valueBuiltin);
        closureClass.addMethod("value:value:value:value:value:value:", valueBuiltin);
        closureClass.addMethod("value:value:value:value:value:value:value:", valueBuiltin);
        closureClass.addMethodFromSource("whileTrue: body\n[\n    [\n        (self value) ifFalse: [ ^ self ].\n        body value.\n    ] repeat.\n]\n");

        setValue("CompiledMethod", compiledMethodClass);
        compiledMethodClass.addMethod("source", (fiber, args) -> fiber.setAccu(new STString(((STCompiledMethod) args[0]).getSource())));

        setValue("Port", portClass);
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
        portClass.addMethod("outputChar:", (fiber, args) -> {
            ((STPort) args[0]).write("" + ((STCharacter) args[1]).getValue());
        });
        portClass.addMethodFromSource("out: obj\n[\n    obj writeOn: self.\n  ^ self\n]\n");
        portClass.addMethodFromSource("store: obj\n[\n    obj storeOn: self.\n  ^ self\n]\n");
        portClass.addMethodFromSource("cr\n[\n    self out: '\n'.\n]\n");
        portClass.addMethod("edit:title:", (fiber, args) -> {
            fiber.pause();
            ((STPort) args[0]).edit(((STString) args[2]).getValue(), ((STString) args[1]).getValue(), (v) -> {
                if (v == null) fiber.restartWithValue(STNil.get());
                else fiber.restartWithValue(v);
            });
        });
        portClass.addMethodFromSource("edit\n[\n  ^ self edit: '' title: 'Something new'\n]\n");
        portClass.addMethod("uploadToUser:", (fiber, args) -> {
            ((STPort) args[0]).uploadToUser(((STString) args[1]).getValue());
        });
        portClass.addMethod("downloadFromUser", (fiber, args) -> {
            fiber.pause();
            ((STPort) args[0]).downloadFromUser((STString file) -> fiber.restartWithValue(file));
        });
        portClass.addMethod("close", (fiber, args) -> {
            ((STPort) args[0]).close();
        });

        STClass fourClass = objectClass.subclass();
        fourClass.addMethodFromSource("run\n[\n]\n");
        fourClass.addMethodFromSource("newConnection: connection | line\n[\n    Transcript := connection.\n    [\n        line := connection prompt: 'Smalltalk > '.\n        (line = '') ifFalse: [ connection store: line compile value; cr ].\n    ] repeat.\n]\n");

        STObject four = (STObject) fourClass.instantiate();
        setValue("Four", four);
    }
}
