package nijakow.four.smalltalk;

import nijakow.four.smalltalk.net.IConnection;
import nijakow.four.smalltalk.net.MUDConnection;
import nijakow.four.smalltalk.objects.*;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.parser.ParseException;
import nijakow.four.smalltalk.vm.BasicBuiltin;
import nijakow.four.smalltalk.vm.Builtin;
import nijakow.four.smalltalk.vm.FourException;
import nijakow.four.smalltalk.vm.Quickloader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

        objectClass = new STClass(STSymbol.get("Object"));
        setValue("Object", objectClass);
        objectClass.addMethodFromSource("init\n[\n  ^ self\n]\n");

        metaClass = objectClass.subclass(STSymbol.get("Class"), this);
        metaClass.setMetaClass(metaClass);
        metaClass.addMethodFromSource("new\n[\n  ^ <primitive:class/new>\n]\n");
        metaClass.addMethodFromSource("subclass: name\n[\n  ^ <primitive:class/subclass:>\n]\n");
        metaClass.addMethodFromSource("instanceVariableNames: vars\n[\n    <primitive:class/instanceVariableNames:>.\n  ^ self\n]\n");
        metaClass.addMethodFromSource("category: category\n[\n    category := category asSymbol.\n    <primitive:class/category:>.\n  ^ self\n]\n");

        collectionClass = objectClass.subclass(STSymbol.get("Collection"), this);
        sequenceableCollectionClass = collectionClass.subclass(STSymbol.get("SequenceableCollection"), this);
        arrayedCollectionClass = sequenceableCollectionClass.subclass(STSymbol.get("ArrayedCollection"), this);
        outputStreamClass = objectClass.subclass(STSymbol.get("OutputStream"), this);
        numberClass = objectClass.subclass(STSymbol.get("Number"), this);
        integerLikeClass = numberClass.subclass(STSymbol.get("IntegerLike"), this);
        nilClass = objectClass.subclass(STSymbol.get("Nil"), this);
        booleanClass = objectClass.subclass(STSymbol.get("Boolean"), this);
        integerClass = integerLikeClass.subclass(STSymbol.get("Integer"), this);
        characterClass = integerLikeClass.subclass(STSymbol.get("Character"), this);
        stringClass = arrayedCollectionClass.subclass(STSymbol.get("String"), this);
        symbolClass = objectClass.subclass(STSymbol.get("Symbol"), this);
        arrayClass = arrayedCollectionClass.subclass(STSymbol.get("Array"), this);
        closureClass = objectClass.subclass(STSymbol.get("BlockClosure"), this);
        methodClass = objectClass.subclass(STSymbol.get("Method"), this);
        compiledMethodClass = methodClass.subclass(STSymbol.get("CompiledMethod"), this);
        builtinMethodClass = methodClass.subclass(STSymbol.get("BuiltinMethod"), this);
        portClass = outputStreamClass.subclass(STSymbol.get("Port"), this);
        foreignClass = new STClass(STSymbol.get("Foreign"));

        stringClass.addMethodFromSource("asSymbol\n[\n  ^ <primitive:string/asSymbol>\n]\n");
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

        STClass fourClass = objectClass.subclass(null, this);
        fourClass.addMethodFromSource("main\n[\n    '/nijakow/four/smalltalk/classes/Bootstrapper.st' openResource load.\n    Bootstrapper new run.\n]\n");
        fourClass.addMethodFromSource("newConnection: connection\n[\n    Transcript := connection.\n    Apps/Shell launch.\n    Transcript close.\n]\n");

        STObject four = (STObject) fourClass.instantiate();
        setValue("_Four", four);

        addBuiltin("class", (fiber) -> fiber.setAccu(fiber.getSelf().getClass(fiber.getVM().getWorld())));
        addBuiltin("isKindOf", (fiber) -> fiber.setAccu(STBoolean.get(fiber.getSelf().getClass(fiber.getWorld()).isSubclassOf(fiber.getVariable(0).asClass()))));
        addBuiltin("toString", (fiber) -> fiber.setAccu(new STString(fiber.getSelf().toString())));
        addBuiltin("=", (fiber) -> fiber.setAccu(STBoolean.get(fiber.getSelf().is(fiber.getVariable(0)))));
        addBuiltin("throw", (fiber) -> fiber.throwValue(fiber.getSelf()));

        addBuiltin("class/new", (fiber) -> fiber.enter(fiber.getSelf().asClass().instantiate(), "init", new STInstance[]{}));
        addBuiltin("class/name", (fiber) -> fiber.setAccu(STNil.wrap(fiber.getSelf().asClass().getName())));
        addBuiltin("class/instances", (fiber) -> fiber.setAccu(new STArray(STInstance.allInstancesOf(fiber.getSelf().asClass(), fiber.getWorld()))));
        addBuiltin("class/superclass", (fiber) -> fiber.setAccu(STNil.wrap(fiber.getSelf().asClass().getSuperClass())));
        addBuiltin("class/subclass:", (fiber) -> {
            final STSymbol name = fiber.getVariable(0).asSymbol();
            STClass clazz = fiber.getSelf().asClass().subclass(name, fiber.getVM().getWorld());
            fiber.setAccu(clazz);
        });
        addBuiltin("class/instanceVariables", (fiber) -> fiber.setAccu(new STArray(fiber.getSelf().asClass().getInstanceVariableNames())));
        addBuiltin("class/instanceVariableNames:", (fiber) -> fiber.getSelf().asClass().setInstanceVariableNames(fiber.getVariable(0).asString().getValue()));
        addBuiltin("class/category", (fiber) -> fiber.setAccu(fiber.getSelf().asClass().getCategory()));
        addBuiltin("class/category:", (fiber) -> fiber.getSelf().asClass().setCategory(fiber.getVariable(0)));
        addBuiltin("class/method:", (fiber) -> fiber.setAccu(STNil.wrap(fiber.getSelf().asClass().getMethodAsInstance(fiber.getVariable(0).asSymbol()))));
        addBuiltin("class/addMethod:", (fiber) -> {
            try {
                fiber.getSelf().asClass().addMethodFromSource(fiber.getVariable(0).asString().getValue());
                fiber.setAccu(STNil.get());
            } catch (ParseException e) {
                fiber.setAccu(new STString(e.getMessage()));
            }
        });
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
        addBuiltin("string/asBase64", (fiber) -> {
            final byte[] bytes = fiber.getSelf().asString().getValue().getBytes(StandardCharsets.UTF_8);
            try {
                fiber.setAccu(new STString(Base64.getEncoder().encodeToString(bytes)));
            } catch (IllegalArgumentException e) {
                fiber.setAccu(STNil.get());
            }
        });
        addBuiltin("string/fromBase64", (fiber) -> {
            final String value = fiber.getSelf().asString().getValue();
            try {
                fiber.setAccu(new STString(new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8)));
            } catch (Exception e) {
                fiber.setAccu(STNil.get());
            }
        });
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

        addBuiltin("symbol/asString", (fiber) -> fiber.setAccu(fiber.getSelf().asSymbol().getNameAsSTString()));
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
        addBuiltin("method/category", (fiber) -> fiber.setAccu(STNil.wrap(fiber.getSelf().asCompiledMethod().getCategory())));
        addBuiltin("method/doc", (fiber) -> fiber.setAccu(new STString(fiber.getSelf().asCompiledMethod().getDocumentar())));
        addBuiltin("method/source", (fiber) -> fiber.setAccu(new STString(fiber.getSelf().asCompiledMethod().getSource())));

        addBuiltin("port/prompt:", (fiber) -> {
            fiber.pause();
            fiber.getSelf().asPort().prompt(fiber.getVariable(0).asString().getValue());
            fiber.getSelf().asPort().onInput((STString line) -> fiber.restartWithValue(line));
        });
        addBuiltin("port/password:", (fiber) -> {
            fiber.pause();
            fiber.getSelf().asPort().password(((STString) fiber.getVariable(0)).getValue());
            fiber.getSelf().asPort().onInput((STString line) -> fiber.restartWithValue(line));
        });
        addBuiltin("port/smalltalk:", (fiber) -> {
            fiber.pause();
            fiber.getSelf().asPort().smalltalk(((STString) fiber.getVariable(0)).getValue());
            fiber.getSelf().asPort().onInput((STString line) -> fiber.restartWithValue(line));
            fiber.getSelf().asPort().onSmalltalkInput((STString line) -> fiber.restartWithValue(line));
        });
        addBuiltin("port/smalltalk:text:", (fiber) -> {
            fiber.pause();
            fiber.getSelf().asPort().smalltalk(((STString) fiber.getVariable(0)).getValue(), ((STString) fiber.getVariable(1)).getValue());
            fiber.getSelf().asPort().onInput((STString line) -> fiber.restartWithValue(line));
            fiber.getSelf().asPort().onSmalltalkInput((STString line) -> fiber.restartWithValue(line));
        });
        addBuiltin("port/image:width:height:", (fiber) -> {
            fiber.getSelf().asPort().image(((STString) fiber.getVariable(0)).getValue(), ((STInteger) fiber.getVariable(1)).getValue(), ((STInteger) fiber.getVariable(2)).getValue());
        });
        addBuiltin("port/charOut:", (fiber) -> {
            fiber.getSelf().asPort().write("" + fiber.getVariable(0).asCharacter().getValue());
        });
        addBuiltin("port/edit:title:", (fiber) -> {
            fiber.pause();
            fiber.getSelf().asPort().edit(fiber.getVariable(1).asString().getValue(), fiber.getVariable(0).asString().getValue(), (v) -> {
                if (v == null) fiber.restartWithValue(STNil.get());
                else fiber.restartWithValue(v);
            });
        });
        addBuiltin("port/uploadToUser", (fiber) -> {
            fiber.getSelf().asPort().uploadToUser(fiber.getVariable(0).asString().getValue());
        });
        addBuiltin("port/downloadFromUser", (fiber) -> {
            fiber.pause();
            fiber.getSelf().asPort().downloadFromUser((STString file) -> fiber.restartWithValue(file));
        });
        addBuiltin("port/close", (fiber) -> {
            fiber.getSelf().asPort().close();
        });
    }

    public BasicBuiltin getBuiltinFor(STSymbol symbol) {
        return builtins.get(symbol);
    }

    private void addBuiltin(String name, BasicBuiltin builtin) {
        this.builtins.put(STSymbol.get(name), builtin);
    }
}
