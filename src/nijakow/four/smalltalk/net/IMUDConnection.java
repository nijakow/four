package nijakow.four.smalltalk.net;

import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STSymbol;

import java.util.function.Consumer;

public interface IMUDConnection {
    void writeSend(Consumer<STInstance> callback, STInstance receiver, STSymbol message, STInstance... args);
    void writeResult(STInstance value);
    void awaitResult(Consumer<STInstance> consumer);
}
