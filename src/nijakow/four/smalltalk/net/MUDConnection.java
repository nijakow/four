package nijakow.four.smalltalk.net;

import nijakow.four.smalltalk.SmalltalkVM;
import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.FourException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MUDConnection implements IMUDConnection {
    private final SmalltalkVM vm;
    private final IConnection connection;
    private final Map<String, STInstance> knownObjects = new HashMap<>();
    private final Map<String, Consumer<STInstance>> waitingCallbacks = new HashMap<>();
    private long keyCounter;

    public MUDConnection(SmalltalkVM vm, IConnection connection) {
        this.vm = vm;
        this.connection = connection;
        this.connection.onEscape("fourconnect/send", this::handleSend);
        this.connection.onEscape("fourconnect/result", this::handleSend);
    }

    private String generateKey() {
        return "" + (keyCounter++);
    }

    private STInstance decode(String object) {
        return null;    // TODO
    }

    private String encode(STInstance object) {
        return null;    // TODO
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

    private void writeSend(Consumer<STInstance> callback, STInstance receiver, STSymbol message, STInstance... args) {
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
}
