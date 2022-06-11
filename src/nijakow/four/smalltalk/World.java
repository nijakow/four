package nijakow.four.smalltalk;

import nijakow.four.smalltalk.net.IConnection;
import nijakow.four.smalltalk.net.MUDConnection;
import nijakow.four.smalltalk.objects.*;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.parser.ParseException;
import nijakow.four.smalltalk.vm.BasicBuiltin;
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
    private final Map<STSymbol, BasicBuiltin> builtins = new HashMap<>();

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
    private STClass collectionClass;
    private STClass sequenceableCollectionClass;
    private STClass arrayedCollectionClass;
    private STClass outputStreamClass;
    private STClass numberClass;
    private STClass integerLikeClass;

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
        makeSpecial(STSymbol.get("Me"));

        objectClass = new STClass();
        setValue("Object", objectClass);
        objectClass.addMethodFromSource("init\n[\n  ^ self\n]\n");

        metaClass = objectClass.subclass(this);
        metaClass.setMetaClass(metaClass);
        setValue("Class", metaClass);
        metaClass.addMethodFromSource("new\n[\n  ^ <primitive:class/new>\n]\n");
        metaClass.addMethodFromSource("subclass: name\n[\n  ^ <primitive:class/subclass:>\n]\n");
        metaClass.addMethodFromSource("instanceVariableNames: vars\n[\n    <primitive:class/instanceVariableNames:>.\n  ^ self\n]\n");

        collectionClass = objectClass.subclass(this);
        sequenceableCollectionClass = collectionClass.subclass(this);
        arrayedCollectionClass = sequenceableCollectionClass.subclass(this);
        outputStreamClass = objectClass.subclass(this);
        numberClass = objectClass.subclass(this);
        integerLikeClass = numberClass.subclass(this);
        nilClass = objectClass.subclass(this);
        booleanClass = objectClass.subclass(this);
        integerClass = integerLikeClass.subclass(this);
        characterClass = integerLikeClass.subclass(this);
        stringClass = arrayedCollectionClass.subclass(this);
        symbolClass = objectClass.subclass(this);
        arrayClass = arrayedCollectionClass.subclass(this);
        closureClass = objectClass.subclass(this);
        methodClass = objectClass.subclass(this);
        compiledMethodClass = methodClass.subclass(this);
        builtinMethodClass = methodClass.subclass(this);
        portClass = outputStreamClass.subclass(this);
        foreignClass = new STClass();

        setValue("Collection", collectionClass);
        setValue("SequenceableCollection", sequenceableCollectionClass);
        setValue("ArrayedCollection", arrayedCollectionClass);
        setValue("OutputStream", outputStreamClass);

        setValue("Nil", nilClass);

        setValue("Boolean", booleanClass);

        setValue("Number", numberClass);
        setValue("IntegerLike", integerLikeClass);
        setValue("Integer", integerClass);

        setValue("Character", characterClass);

        setValue("String", stringClass);
        stringClass.addMethodFromSource("+ other\n[\n  ^ <primitive:string/plus:>\n]\n");
        stringClass.addMethodFromSource("load\n[\n  ^ <primitive:string/load>\n]\n");
        stringClass.addMethodFromSource("openResource\n[\n  ^ <primitive:string/openResource>\n]\n");
        /*stringClass.addMethod("connect:", (fiber, args) -> {
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
        });*/

        setValue("Symbol", symbolClass);

        setValue("Array", arrayClass);
        arrayClass.setInstantiator(() -> new STArray(0));

        setValue("Method", methodClass);
        Builtin valueBuiltin = (fiber, args) -> {
            fiber.loadLexicalSelf(args[0].asClosure().getContext());
            fiber.push();
            for (int x = 1; x < args.length; x++)
                fiber.push(args[x]);
            args[0].asClosure().execute(fiber, args.length - 1);
        };

        setValue("BlockClosure", closureClass);
        closureClass.addMethodFromSource("value\n[\n  ^ <primitive:closure/value>\n]\n");
        for (int x = 1; x < 8; x++) {
            StringBuilder s = new StringBuilder();
            for (int y = 0; y < x; y++) {
                s.append("value: v");
                s.append(y + 1);
                s.append(' ');
            }
            s.append("\n[\n  ^ <primitive:closure/value>\n]\n");
            closureClass.addMethodFromSource(s.toString());
        }
        closureClass.addMethodFromSource("whileTrue: body\n[\n    [\n        (self value) ifFalse: [ ^ self ].\n        body value.\n    ] repeat.\n]\n");

        setValue("BuiltinMethod", builtinMethodClass);
        setValue("CompiledMethod", compiledMethodClass);

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
        portClass.addMethod("smalltalk:text:", (fiber, args) -> {
            fiber.pause();
            args[0].asPort().smalltalk(((STString) args[1]).getValue(), ((STString) args[2]).getValue());
            args[0].asPort().onInput((STString line) -> fiber.restartWithValue(line));
            args[0].asPort().onSmalltalkInput((STString line) -> fiber.restartWithValue(line));
        });
        portClass.addMethod("image:width:height:", (fiber, args) -> {
            args[0].asPort().image(((STString) args[1]).getValue(), ((STInteger) args[2]).getValue(), ((STInteger) args[3]).getValue());
        });
        portClass.addMethod("charOut:", (fiber, args) -> {
            args[0].asPort().write("" + args[1].asCharacter().getValue());
        });
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

        STClass fourClass = objectClass.subclass(this);
        fourClass.addMethodFromSource("main\n[\n    '/nijakow/four/smalltalk/classes/Bootstrapper.st' openResource load.\n    Bootstrapper new run.\n]\n");
        fourClass.addMethodFromSource("newConnection: connection\n[\n    Transcript := connection.\n    Apps/Browser launch.\n    Transcript close.\n]\n");

        STObject four = (STObject) fourClass.instantiate();
        setValue("Four", four);

        addBuiltin("class", (fiber) -> fiber.setAccu(fiber.getSelf().getClass(fiber.getVM().getWorld())));
        addBuiltin("isKindOf", (fiber) -> fiber.setAccu(STBoolean.get(fiber.getSelf().getClass(fiber.getWorld()).isSubclassOf(fiber.getVariable(0).asClass()))));
        addBuiltin("toString", (fiber) -> fiber.setAccu(new STString(fiber.getSelf().toString())));
        addBuiltin("=", (fiber) -> fiber.setAccu(STBoolean.get(fiber.getSelf().is(fiber.getVariable(0)))));
        addBuiltin("throw", (fiber) -> fiber.throwValue(fiber.getSelf()));

        addBuiltin("class/new", (fiber) -> fiber.enter(fiber.getSelf().asClass().instantiate(), "init", new STInstance[]{}));
        addBuiltin("class/instances", (fiber) -> fiber.setAccu(new STArray(STInstance.allInstancesOf(fiber.getSelf().asClass(), fiber.getWorld()))));
        addBuiltin("class/superclass", (fiber) -> fiber.setAccu(STNil.wrap(fiber.getSelf().asClass().getSuperClass())));
        addBuiltin("class/subclass:", (fiber) -> {
            final STSymbol name = fiber.getVariable(0).asSymbol();
            STClass clazz = fiber.getSelf().asClass().subclass(fiber.getVM().getWorld());
            fiber.getVM().getWorld().setValue(name, clazz);
            fiber.setAccu(clazz);
        });
        addBuiltin("class/instanceVariables", (fiber) -> fiber.setAccu(new STArray(fiber.getSelf().asClass().getInstanceVariableNames())));
        addBuiltin("class/instanceVariableNames:", (fiber) -> fiber.getSelf().asClass().setInstanceVariableNames(fiber.getVariable(0).asString().getValue()));
        addBuiltin("class/category", (fiber) -> fiber.setAccu(fiber.getSelf().asClass().getCategory()));
        addBuiltin("class/category:", (fiber) -> fiber.getSelf().asClass().setCategory(fiber.getVariable(0)));
        addBuiltin("class/method:", (fiber) -> fiber.setAccu(STNil.wrap(fiber.getSelf().asClass().getMethodAsInstance(fiber.getVariable(0).asSymbol()))));
        addBuiltin("class/addMethod:", (fiber) -> fiber.getSelf().asClass().addMethodFromSource(fiber.getVariable(0).asString().getValue()));
        addBuiltin("class/removeMethod:", (fiber) -> fiber.getSelf().asClass().removeMethod(fiber.getVariable(0).asSymbol()));
        addBuiltin("class/selectors", (fiber) -> fiber.setAccu(new STArray(fiber.getSelf().asClass().getSelectors())));
        addBuiltin("class/handle:do:", (fiber) -> {
            final STInstance self = fiber.getSelf();
            final STInstance handler = fiber.getVariable(1);
            fiber.loadSelf();
            fiber.push();
            fiber.getVariable(0).asClosure().execute(fiber, 0);
            fiber.top().setHandler(self.asClass(), handler.asClosure());
        });

        addBuiltin("integer/asChar", (fiber) -> fiber.setAccu(STCharacter.get((char) fiber.getSelf().asInteger().getValue())));
        addBuiltin("integer/plus:", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asInteger().getValue() + fiber.getVariable(0).asInteger().getValue())));
        addBuiltin("integer/minus:", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asInteger().getValue() - fiber.getVariable(0).asInteger().getValue())));
        addBuiltin("integer/mul:", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asInteger().getValue() * fiber.getVariable(0).asInteger().getValue())));
        addBuiltin("integer/div:", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asInteger().getValue() / fiber.getVariable(0).asInteger().getValue())));
        addBuiltin("integer/mod:", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asInteger().getValue() % fiber.getVariable(0).asInteger().getValue())));
        addBuiltin("integer/bitAnd:", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asInteger().getValue() & fiber.getVariable(0).asInteger().getValue())));
        addBuiltin("integer/bitOr:", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asInteger().getValue() | fiber.getVariable(0).asInteger().getValue())));
        addBuiltin("integer/bitXor:", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asInteger().getValue() ^ fiber.getVariable(0).asInteger().getValue())));
        addBuiltin("integer/bitNot", (fiber) -> fiber.setAccu(STInteger.get(~fiber.getSelf().asInteger().getValue())));
        addBuiltin("integer/leftShift:", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asInteger().getValue() << fiber.getVariable(0).asInteger().getValue())));
        addBuiltin("integer/rightShift:", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asInteger().getValue() >> fiber.getVariable(0).asInteger().getValue())));
        addBuiltin("integer/less:", (fiber) -> fiber.setAccu(STBoolean.get(fiber.getSelf().asInteger().getValue() < fiber.getVariable(0).asInteger().getValue())));

        addBuiltin("char/asInt", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asCharacter().getValue())));
        addBuiltin("char/asString", (fiber) -> fiber.setAccu(new STString(fiber.getSelf().asCharacter().getValue() + "")));
        addBuiltin("char/plus:", (fiber) -> fiber.setAccu(STCharacter.get((char) (fiber.getSelf().asCharacter().getValue() + fiber.getVariable(0).asInteger().getValue()))));
        addBuiltin("char/minus:", (fiber) -> fiber.setAccu(STCharacter.get((char) (fiber.getSelf().asCharacter().getValue() - fiber.getVariable(0).asInteger().getValue()))));
        addBuiltin("char/less:", (fiber) -> fiber.setAccu(STBoolean.get(fiber.getSelf().asCharacter().getValue() < fiber.getVariable(0).asCharacter().getValue())));

        addBuiltin("string/size", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asString().getValue().length())));
        addBuiltin("string/at:", (fiber) -> fiber.setAccu(STCharacter.get(fiber.getSelf().asString().getValue().charAt(fiber.getVariable(0).asInteger().getValue() - 1))));
        addBuiltin("string/from:to:", (fiber) -> fiber.setAccu(new STString(fiber.getSelf().asString().getValue().substring(fiber.getVariable(0).asInteger().getValue() - 1, fiber.getVariable(1).asInteger().getValue() - 1))));
        addBuiltin("string/plus:", (fiber) -> fiber.setAccu(new STString(fiber.getSelf().asString().getValue() + fiber.getVariable(0).asString().getValue())));
        addBuiltin("string/charplus:", (fiber) -> fiber.setAccu(new STString(fiber.getSelf().asString().getValue() + fiber.getVariable(0).asCharacter().getValue())));
        addBuiltin("string/compile", (fiber) -> fiber.setAccu(new STClosure(fiber.getSelf().asString().compile(), null)));
        addBuiltin("string/asSymbol", (fiber) -> fiber.setAccu(STSymbol.get(fiber.getSelf().asString().getValue())));
        addBuiltin("string/less:", (fiber) -> fiber.setAccu(STBoolean.get(fiber.getSelf().asString().getValue().compareTo(fiber.getVariable(0).asString().getValue()) < 0)));
        addBuiltin("string/leq:", (fiber) -> fiber.setAccu(STBoolean.get(fiber.getSelf().asString().getValue().compareTo(fiber.getVariable(0).asString().getValue()) <= 0)));
        addBuiltin("string/load", (fiber) -> {
            final Quickloader quickloader = new Quickloader(new ByteArrayInputStream(fiber.getSelf().asString().getValue().getBytes(StandardCharsets.UTF_8)));
            quickloader.loadInto(fiber.getVM(), fiber.getVM().getWorld());
            fiber.setAccu(STBoolean.getTrue());
        });
        addBuiltin("string/openResource", (fiber) -> {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(fiber.getSelf().asString().getValue())));
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

        addBuiltin("symbol/asString", (fiber) -> fiber.setAccu(new STString(fiber.getSelf().asSymbol().getName())));
        addBuiltin("symbol/globalValue", (fiber) -> fiber.setAccu(fiber.getWorld().getValue(fiber.getSelf().asSymbol().getName())));
        addBuiltin("symbol/globalValue:", (fiber) -> fiber.getWorld().setValue(fiber.getSelf().asSymbol(), fiber.getVariable(0)));

        addBuiltin("array/new", (fiber) -> fiber.setAccu(new STArray(fiber.getVariable(0).asInteger().getValue())));
        addBuiltin("array/size", (fiber) -> fiber.setAccu(STInteger.get(fiber.getSelf().asArray().getSize())));
        addBuiltin("array/at:", (fiber) -> fiber.setAccu(fiber.getSelf().asArray().get(fiber.getVariable(0).asInteger().getValue() - 1)));
        addBuiltin("array/at:put:", (fiber) -> fiber.getSelf().asArray().set(fiber.getVariable(0).asInteger().getValue() - 1, fiber.getVariable(1)));
        addBuiltin("array/constructString", (fiber) -> {
            final STArray array = fiber.getSelf().asArray();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.getSize(); i++)
                sb.append(array.get(i).asCharacter().getValue());
            fiber.setAccu(new STString(sb.toString()));
        });

        addBuiltin("closure/value", (fiber) -> {
            fiber.loadLexicalSelf(fiber.getSelf().asClosure().getContext());
            fiber.push();
            for (int x = 0; x < fiber.getArgs(); x++)
                fiber.push(fiber.getVariable(x));
            fiber.getSelf().asClosure().execute(fiber, fiber.getArgs());
        });

        addBuiltin("method/holdingClass", (fiber) -> fiber.setAccu(STNil.wrap(fiber.getSelf().asCompiledMethod().getHoldingClass())));
        addBuiltin("method/name", (fiber) -> fiber.setAccu(STNil.wrap(fiber.getSelf().asCompiledMethod().getName())));
        addBuiltin("method/doc", (fiber) -> fiber.setAccu(new STString(fiber.getSelf().asCompiledMethod().getDocumentar())));
        addBuiltin("method/source", (fiber) -> fiber.setAccu(new STString(fiber.getSelf().asCompiledMethod().getSource())));
    }

    public BasicBuiltin getBuiltinFor(STSymbol symbol) {
        return builtins.get(symbol);
    }

    private void addBuiltin(String name, BasicBuiltin builtin) {
        this.builtins.put(STSymbol.get(name), builtin);
    }
}
