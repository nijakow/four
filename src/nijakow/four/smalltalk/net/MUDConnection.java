package nijakow.four.smalltalk.net;

import nijakow.four.smalltalk.SmalltalkVM;
import nijakow.four.smalltalk.objects.*;
import nijakow.four.smalltalk.vm.FourException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MUDConnection implements IMUDConnection {
    private final SmalltalkVM vm;
    private final IConnection connection;
    private final Map<String, Consumer<STInstance>> waitingCallbacks = new HashMap<>();
    private final Map<STInstance, String> forwardObjects = new HashMap<>();
    private final Map<String, STInstance> backwardObjects = new HashMap<>();
    private final Map<String, STForeign> foreigns = new HashMap<>();
    private long keyCounter;

    public MUDConnection(SmalltalkVM vm, IConnection connection) {
        this.vm = vm;
        this.connection = connection;
        this.connection.onEscape("fourconnect/send", this::handleSend);
        this.connection.onEscape("fourconnect/supersend", this::handleSuperSend);
        this.connection.onEscape("fourconnect/result", this::handleResult);
    }

    private String generateKey() {
        return "" + (keyCounter++);
    }

    private STInstance decode(String object) {
        int index = object.indexOf(':');
        final String id = object.substring(0, index);
        final String data = object.substring(index + 1);
        switch (id)
        {
            case "nil": return STNil.get();
            case "boolean": return STBoolean.get("true".equals(data));
            case "int": return STInteger.get(Integer.parseInt(data));
            case "char": return STCharacter.get((char) Integer.parseInt(data));
            case "string": return new STString(data);
            case "symbol": return STSymbol.get(data);
            case "foreign": return decodeForeign(data);
            case "backward": return decodeBackward(data);
            default: return STNil.get();
        }
    }

    private String encode(STInstance object) {
        /*
         * Using instanceof is bad style, but the approach
         * works for now. Famous last words, I know... :)
         *                                   - nijakow
         */
        if (object instanceof STInteger)
            return "int:" + ((STInteger) object).getValue();
        else if (object instanceof STCharacter)
            return "char:" + (int) ((STCharacter) object).getValue();
        else if (object instanceof STString)
            return "string:" + ((STString) object).getValue();
        else if (object instanceof STSymbol)
            return "symbol:" + ((STSymbol) object).getName();
        else if (object instanceof STBoolean)
            return "boolean:" + ((STBoolean) object).isTrue();
        else if (object instanceof STNil)
            return "nil:";
        else if (object instanceof STForeign) {
            /*
             * Make sure that this object actually comes from us.
             */
            assert ((STForeign) object).getConnection() == this;
            return "backward:" + ((STForeign) object).getID();
        }
        else
            return "foreign:" + encodeForeign(object);
    }

    private String encodeForeign(STInstance object) {
        if (forwardObjects.containsKey(object))
            return forwardObjects.get(object);
        final String key = generateKey();
        forwardObjects.put(object, key);
        backwardObjects.put(key, object);
        return key;
    }

    private STInstance decodeBackward(String id) {
        return backwardObjects.getOrDefault(id, STNil.get());
    }

    private STForeign decodeForeign(String id) {
        if (foreigns.containsKey(id))
            return foreigns.get(id);
        STForeign foreign = new STForeign(this, id);
        foreigns.put(id, foreign);
        return foreign;
    }

    private boolean handleSend(String[] params) {
        try {
            final String key = params[1];
            STInstance receiver = decode(params[2]);
            STSymbol message = decode(params[3]).asSymbol();
            STInstance[] arguments = new STInstance[params.length - 4];
            for (int i = 0; i < arguments.length; i++)
                arguments[i] = decode(params[i + 4]);
            vm.startFiber(receiver, message, arguments).onExit((value) -> writeResult(key, value));
        } catch (FourException e) {
            return false;
        }
        return true;
    }

    private boolean handleSuperSend(String[] params) {
        try {
            final String key = params[1];
            STInstance receiver = decode(params[2]);
            STSymbol message = decode(params[3]).asSymbol();
            STInstance[] arguments = new STInstance[params.length - 4];
            for (int i = 0; i < arguments.length; i++)
                arguments[i] = decode(params[i + 4]);
            vm.startFiberUsingSuperSend(receiver, message, arguments).onExit((value) -> writeResult(key, value));
        } catch (FourException e) {
            return false;
        }
        return true;
    }

    public void awaitResult(Consumer<STInstance> consumer) {
        waitingCallbacks.put("", consumer);
    }

    private void handleResult(String[] params) {
        final String key = params[1];
        final STInstance value = decode(params[2]);
        Consumer<STInstance> callback = waitingCallbacks.get(key);
        if (callback != null)
            callback.accept(value);
    }

    private void writeResult(String key, STInstance value) {
        connection.writeEscaped("fourconnect/result", key, encode(value));
    }

    public void writeResult(STInstance value) {
        writeResult("", value);
    }

    public void writeSend(Consumer<STInstance> callback, STInstance receiver, STSymbol message, STInstance... args) {
        final String key = generateKey();
        waitingCallbacks.put(key, callback);
        String[] encodedArgs = new String[args.length + 4];
        encodedArgs[0] = "fourconnect/send";
        encodedArgs[1] = key;
        encodedArgs[2] = encode(receiver);
        encodedArgs[3] = encode(message);
        for (int i = 0; i < args.length; i++)
            encodedArgs[i + 4] = encode(args[i]);
        connection.writeEscaped(encodedArgs);
    }

    public void writeSuperSend(Consumer<STInstance> callback, STInstance receiver, STSymbol message, STInstance... args) {
        final String key = generateKey();
        waitingCallbacks.put(key, callback);
        String[] encodedArgs = new String[args.length + 4];
        encodedArgs[0] = "fourconnect/supersend";
        encodedArgs[1] = key;
        encodedArgs[2] = encode(receiver);
        encodedArgs[3] = encode(message);
        for (int i = 0; i < args.length; i++)
            encodedArgs[i + 4] = encode(args[i]);
        connection.writeEscaped(encodedArgs);
    }
}
