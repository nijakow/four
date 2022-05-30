package nijakow.four.smalltalk;

import nijakow.four.smalltalk.net.IConnection;
import nijakow.four.smalltalk.net.MUDConnection;
import nijakow.four.smalltalk.objects.*;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.parser.ParseException;
import nijakow.four.smalltalk.vm.Builtin;
import nijakow.four.smalltalk.vm.Quickloader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class World {
    private final Map<STSymbol, STInstance> bindings = new HashMap<>();
    private final Set<STSymbol> specialSymbols = new HashSet<>();

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
    private STClass foreignClass;
    private STClass exceptionClass;
    private STClass collectionClass;
    private STClass sequenceableCollectionClass;
    private STClass arrayedCollectionClass;

    public World() {
    }

    public STInstance getValue(String name) {
        return getValue(STSymbol.get(name));
    }

    public STInstance getValue(STSymbol symbol) {
        return bindings.getOrDefault(symbol, STNil.get());
    }

    public boolean isSpecial(STSymbol symbol) {
        return specialSymbols.contains(symbol);
    }

    public void makeSpecial(STSymbol symbol) {
        if (symbol != null)
            specialSymbols.add(symbol);
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

    public STClass getForeignClass() {
        return foreignClass;
    }

    public void buildDefaultWorld() throws ParseException {
        makeSpecial(STSymbol.get("Transcript"));

        objectClass = new STClass();
        setValue("Object", objectClass);
        objectClass.addMethodFromSource("init\n[\n]\n");
        objectClass.addMethod("value", (fiber, args) -> fiber.setAccu(args[0]));
        objectClass.addMethod("class", (fiber, args) -> fiber.setAccu(args[0].getClass(fiber.getVM().getWorld())));
        objectClass.addMethod("isKindOf:", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].getClass(fiber.getVM().getWorld()).isSubclassOf(args[1].asClass()))));
        objectClass.addMethod("asBool", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].isTrue())));
        objectClass.addMethod("toString", (fiber, args) -> fiber.setAccu(new STString(args[0].toString())));
        objectClass.addMethod("=", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].is(args[1]))));
        objectClass.addMethod("!=", (fiber, args) -> fiber.setAccu(STBoolean.get(!args[0].is(args[1]))));
        objectClass.addMethodFromSource("writeOn: w\n[\n    self storeOn: w\n]\n");
        objectClass.addMethodFromSource("storeOn: w\n[\n    w out: self toString\n]\n");

        metaClass = objectClass.subclass();
        setValue("Class", metaClass);
        metaClass.addMethod("new", (fiber, args) -> {
            fiber.enter(args[0].asClass().instantiate(), "init", new STInstance[]{});
        });
        metaClass.addMethod("new:", (fiber, args) -> {
            fiber.enter(args[0].asClass().instantiate(args[1]), "init", new STInstance[]{});
        });
        metaClass.addMethod("instances", (fiber, args) -> {
            STInstance[] instances = STInstance.allInstancesOf(args[0].asClass(), fiber.getVM().getWorld());
            fiber.setAccu(new STArray(instances));
        });
        metaClass.addMethod("parent", (fiber, args) -> {
            STClass parent = args[0].asClass().getSuperClass();
            if (parent == null)
                fiber.setAccu(STNil.get());
            else
                fiber.setAccu(parent);
        });
        metaClass.addMethod("subclass:", (fiber, args) -> {
            final STSymbol name = args[1].asSymbol();
            STClass clazz = args[0].asClass().subclass();
            setValue(name, clazz);
            fiber.setAccu(clazz);
        });
        metaClass.addMethod("instanceVariables", (fiber, args) -> {
            fiber.setAccu(new STArray(args[0].asClass().getInstanceVariableNames()));
        });
        metaClass.addMethod("instanceVariableNames:", (fiber, args) -> {
            args[0].asClass().setInstanceVariableNames(args[1].asString().getValue());
        });
        metaClass.addMethod("method:", (fiber, args) -> {
            STMethod method = args[0].asClass().getMethod(args[1].asSymbol());
            if (method == null || method.asInstance() == null)
                fiber.setAccu(STNil.get());
            else
                fiber.setAccu(method.asInstance());
        });
        metaClass.addMethodFromSource(">> m\n[\n  ^ self method: m\n]\n");
        metaClass.addMethod("addMethod:", (fiber, args) -> {
            if (args[1] != STNil.get())
                args[0].asClass().addMethodFromSource(args[1].asString().getValue());
        });
        metaClass.addMethod("removeMethod:", (fiber, args) -> {
            args[0].asClass().removeMethod(args[1].asSymbol());
        });
        metaClass.addMethod("category", (fiber, args) -> fiber.setAccu(STString.wrap(args[0].asClass().getCategory())));
        metaClass.addMethod("category:", (fiber, args) -> args[0].asClass().setCategory(args[1].asString().getValue()));
        metaClass.addMethod("selectors", (fiber, args) -> {
            STSymbol[] selectors = args[0].asClass().getSelectors();
            STArray result = new STArray(selectors);
            fiber.setAccu(result);
        });
        metaClass.addMethod("handle:do:", (fiber, args) -> {
            args[1].asClosure().execute(fiber, 0);
            fiber.top().setHandler(args[2].asClosure());
        });
        metaClass.addMethodFromSource("edit\n[\n    self addMethod: Transcript edit.\n]\n");
        metaClass.addMethodFromSource("edit: name | text\n[\n    ((self method: name) = nil) ifTrue: [\n        text := (name asString) + ' | \"Local variables\"\\n[\\n  ^ self\\n]\\n'.\n    ] ifFalse: [\n        text := (self method: name) source.\n    ].\n    text := (Transcript edit: text title: ('Method ' + (name asString))).\n    (text = nil)  ifTrue: [ ^ self ].\n    (text isWhitespace) ifTrue: [ self removeMethod: name ]\n                  ifFalse: [ self addMethod: text ].\n  ^ self\n]\n");

        collectionClass = objectClass.subclass();
        sequenceableCollectionClass = collectionClass.subclass();
        arrayedCollectionClass = collectionClass.subclass();
        nilClass = objectClass.subclass();
        booleanClass = objectClass.subclass();
        integerClass = objectClass.subclass();
        characterClass = objectClass.subclass();
        stringClass = arrayedCollectionClass.subclass();
        symbolClass = objectClass.subclass();
        arrayClass = arrayedCollectionClass.subclass();
        closureClass = objectClass.subclass();
        methodClass = objectClass.subclass();
        compiledMethodClass = methodClass.subclass();
        builtinMethodClass = methodClass.subclass();
        portClass = objectClass.subclass();
        exceptionClass = objectClass.subclass();
        foreignClass = new STClass();

        setValue("Collection", collectionClass);
        setValue("SequenceableCollection", sequenceableCollectionClass);
        setValue("ArrayedCollection", arrayedCollectionClass);

        setValue("Nil", nilClass);

        setValue("Boolean", booleanClass);
        booleanClass.addMethod("not", (fiber, args) -> fiber.setAccu(STBoolean.get(!args[0].isTrue())));
        booleanClass.addMethod("&", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].isTrue() && args[1].isTrue())));
        booleanClass.addMethod("|", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].isTrue() || args[1].isTrue())));

        setValue("Integer", integerClass);
        integerClass.addMethod("asChar", (fiber, args) -> fiber.setAccu(STCharacter.get((char) args[0].asInteger().getValue())));
        integerClass.addMethod("+", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asInteger().getValue() + args[1].asInteger().getValue())));
        integerClass.addMethod("-", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asInteger().getValue() - args[1].asInteger().getValue())));
        integerClass.addMethod("*", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asInteger().getValue() * args[1].asInteger().getValue())));
        integerClass.addMethod("/", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asInteger().getValue() / args[1].asInteger().getValue())));
        integerClass.addMethod("mod:", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asInteger().getValue() % args[1].asInteger().getValue())));
        integerClass.addMethod("negate", (fiber, args) -> fiber.setAccu(STInteger.get(-args[0].asInteger().getValue())));
        integerClass.addMethod("bitAnd:", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asInteger().getValue() & args[1].asInteger().getValue())));
        integerClass.addMethod("bitOr:", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asInteger().getValue() | args[1].asInteger().getValue())));
        integerClass.addMethod("bitXor:", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asInteger().getValue() ^ args[1].asInteger().getValue())));
        integerClass.addMethod("leftShift:", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asInteger().getValue() << args[1].asInteger().getValue())));
        integerClass.addMethod("rightShift:", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asInteger().getValue() >> args[1].asInteger().getValue())));
        integerClass.addMethod("bitNot", (fiber, args) -> fiber.setAccu(STInteger.get(~args[0].asInteger().getValue())));
        integerClass.addMethod("<", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].asInteger().getValue() < args[1].asInteger().getValue())));
        integerClass.addMethod("<=", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].asInteger().getValue() <= args[1].asInteger().getValue())));
        integerClass.addMethod(">", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].asInteger().getValue() > args[1].asInteger().getValue())));
        integerClass.addMethod(">=", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].asInteger().getValue() >= args[1].asInteger().getValue())));
        integerClass.addMethodFromSource("to: upper do: block | v\n[\n    v := self.\n    [ v <= upper ] whileTrue: [\n        block value: v.\n        v := v + 1.\n    ].\n  ^ self\n]\n");

        setValue("Character", characterClass);
        characterClass.addMethod("asInt", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asCharacter().getValue())));
        characterClass.addMethod("isWhitespace", (fiber, args) -> fiber.setAccu(STBoolean.get(Character.isWhitespace(args[0].asCharacter().getValue()))));
        characterClass.addMethod("<", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].asCharacter().getValue() < args[1].asCharacter().getValue())));
        characterClass.addMethod("<=", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].asCharacter().getValue() <= args[1].asCharacter().getValue())));
        characterClass.addMethod(">", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].asCharacter().getValue() > args[1].asCharacter().getValue())));
        characterClass.addMethod(">=", (fiber, args) -> fiber.setAccu(STBoolean.get(args[0].asCharacter().getValue() >= args[1].asCharacter().getValue())));
        characterClass.addMethodFromSource("writeOn: w\n[\n    w outputChar: self\n]\n");

        setValue("String", stringClass);
        stringClass.addMethod("size", (fiber, args) -> fiber.setAccu(STInteger.get((args[0].asString().getValue().length()))));
        stringClass.addMethod("at:", (fiber, args) -> fiber.setAccu(STCharacter.get((args[0].asString().getValue().charAt(args[1].asInteger().getValue() - 1)))));
        stringClass.addMethod("from:to:", (fiber, args) -> fiber.setAccu(new STString((args[0].asString().getValue().substring(args[1].asInteger().getValue() - 1, args[2].asInteger().getValue() - 1)))));
        stringClass.addMethod("+", (fiber, args) -> fiber.setAccu(new STString(args[0].asString().getValue() + args[1].asString().getValue())));
        stringClass.addMethod("compile", (fiber, args) -> fiber.setAccu(new STClosure(args[0].asString().compile(), null)));
        stringClass.addMethod("asSymbol", (fiber, args) -> fiber.setAccu(STSymbol.get(args[0].asString().getValue())));
        stringClass.addMethodFromSource("writeOn: w\n[\n    self do: [ :c | w out: c ]\n]\n");
        stringClass.addMethodFromSource("storeOn: w\n[\n    w out: $'.\n    self do: [ :c | w out: c ].\n    w out: $'.\n]\n");
        stringClass.addMethodFromSource("isWhitespace\n[\n    self do: [ :c | (c isWhitespace) ifFalse: [ ^ false ] ].\n  ^ true\n]\n");
        stringClass.addMethod("load", (fiber, args) -> {
            final Quickloader quickloader = new Quickloader(new ByteArrayInputStream(args[0].asString().getValue().getBytes(StandardCharsets.UTF_8)));
            quickloader.loadInto(fiber.getVM(), fiber.getVM().getWorld());
            fiber.setAccu(STBoolean.getTrue());
        });
        stringClass.addMethod("openResource", (fiber, args) -> {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(args[0].asString().getValue())));
            final StringBuilder contents = new StringBuilder();
            while (true) {
                int i = 0;
                try {
                    i = reader.read();
                } catch (IOException e) {
                    break;
                }
                if (i < 0) break;
                contents.append((char) i);
            }
            fiber.setAccu(new STString(contents.toString()));
        });
        stringClass.addMethod("connect:", (fiber, args) -> {
            try {
                IConnection connection = fiber.getVM().getFour().getServer().connectTo(args[0].asString().getValue(), args[1].asInteger().getValue());
                MUDConnection mudConnection = new MUDConnection(fiber.getVM(), connection);
                fiber.pause();
                mudConnection.awaitResult(fiber::restartWithValue);
            } catch (IOException e) {
                fiber.setAccu(STNil.get());
            }
        });
        stringClass.addMethod("serve:on:", (fiber, args) -> {
            try {
                fiber.getVM().getFour().fourconnectOn(args[0].asString().getValue(), args[2].asInteger().getValue(), args[1]);
                fiber.setAccu(STBoolean.getTrue());
            } catch (IOException e) {
                fiber.setAccu(STBoolean.getFalse());
            }
        });

        setValue("Symbol", symbolClass);
        symbolClass.addMethod("asString", (fiber, args) -> fiber.setAccu(new STString(args[0].asSymbol().getName())));
        symbolClass.addMethod("globalValue", (fiber, args) -> fiber.setAccu(fiber.getVM().getWorld().getValue(args[0].asSymbol())));
        symbolClass.addMethod("globalValue:", (fiber, args) -> fiber.getVM().getWorld().setValue(args[0].asSymbol(), args[1]));

        setValue("Array", arrayClass);
        arrayClass.setInstantiator(() -> new STArray(0));
        arrayClass.setInstantiator2((size) -> new STArray(((STInteger) size).getValue()));
        arrayClass.addMethod("size", (fiber, args) -> fiber.setAccu(STInteger.get(args[0].asArray().getSize())));
        arrayClass.addMethod("at:", (fiber, args) -> fiber.setAccu(args[0].asArray().get(args[1].asInteger().getValue() - 1)));
        arrayClass.addMethod("at:put:", (fiber, args) -> args[0].asArray().set(args[1].asInteger().getValue() - 1, args[2]));

        setValue("Method", methodClass);
        Builtin valueBuiltin = (fiber, args) -> {
            fiber.loadLexicalSelf(args[0].asClosure().getContext());
            fiber.push();
            for (int x = 1; x < args.length; x++)
                fiber.push(args[x]);
            args[0].asClosure().execute(fiber, args.length - 1);
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
        compiledMethodClass.addMethod("holdingClass", (fiber, args) -> {
            STClass name = args[0].asCompiledMethod().getHoldingClass();
            fiber.setAccu(name == null ? STNil.get() : name);
        });
        compiledMethodClass.addMethod("name", (fiber, args) -> {
            STSymbol name = args[0].asCompiledMethod().getName();
            fiber.setAccu(name == null ? STNil.get() : name);
        });
        compiledMethodClass.addMethod("source", (fiber, args) -> fiber.setAccu(new STString(args[0].asCompiledMethod().getSource())));

        setValue("Port", portClass);
        portClass.addMethod("prompt:", (fiber, args) -> {
            fiber.pause();
            args[0].asPort().prompt(args[1].asString().getValue());
            args[0].asPort().onInput((STString line) -> fiber.restartWithValue(line));
        });
        portClass.addMethod("password:", (fiber, args) -> {
            fiber.pause();
            args[0].asPort().password(((STString) args[1]).getValue());
            args[0].asPort().onInput((STString line) -> fiber.restartWithValue(line));
        });
        portClass.addMethod("smalltalk:", (fiber, args) -> {
            fiber.pause();
            args[0].asPort().smalltalk(((STString) args[1]).getValue());
            args[0].asPort().onInput((STString line) -> fiber.restartWithValue(line));
            args[0].asPort().onSmalltalkInput((STString line) -> fiber.restartWithValue(line));
        });
        portClass.addMethod("outputChar:", (fiber, args) -> {
            args[0].asPort().write("" + args[1].asCharacter().getValue());
        });
        portClass.addMethodFromSource("show: obj\n[\n    self out: obj.\n  ^ self\n]\n");
        portClass.addMethodFromSource("out: obj\n[\n    obj writeOn: self.\n  ^ self\n]\n");
        portClass.addMethodFromSource("store: obj\n[\n    obj storeOn: self.\n  ^ self\n]\n");
        portClass.addMethodFromSource("cr\n[\n    self out: '\n'.\n]\n");
        portClass.addMethod("edit:title:", (fiber, args) -> {
            fiber.pause();
            args[0].asPort().edit(args[2].asString().getValue(), args[1].asString().getValue(), (v) -> {
                if (v == null) fiber.restartWithValue(STNil.get());
                else fiber.restartWithValue(v);
            });
        });
        portClass.addMethodFromSource("edit\n[\n  ^ self edit: '' title: 'Something new'\n]\n");
        portClass.addMethod("uploadToUser:", (fiber, args) -> {
            args[0].asPort().uploadToUser(args[1].asString().getValue());
        });
        portClass.addMethod("downloadFromUser", (fiber, args) -> {
            fiber.pause();
            args[0].asPort().downloadFromUser((STString file) -> fiber.restartWithValue(file));
        });
        portClass.addMethod("close", (fiber, args) -> {
            args[0].asPort().close();
        });

        setValue("Exception", exceptionClass);

        STClass fourClass = objectClass.subclass();
        fourClass.addMethodFromSource("main\n[\n    '/nijakow/four/smalltalk/classes/Bootstrapper.st' openResource load.\n    Bootstrapper new run.\n]\n");
        fourClass.addMethodFromSource("newConnection: connection\n[\n    Transcript := connection.\n    (Shell new) run.\n]\n");

        STObject four = (STObject) fourClass.instantiate();
        setValue("Four", four);
    }
}
