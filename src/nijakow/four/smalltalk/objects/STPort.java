package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.net.IConnection;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class STPort extends STInstance {
    private final IConnection connection;

    public STPort(IConnection connection) {
        this.connection = connection;
    }

    @Override
    public STClass getClass(World world) {
        return world.getPortClass();
    }

    public void onInput(Consumer<STString> callback) {
        this.connection.onInput((line) -> callback.accept(new STString(line)));
    }

    public void write(String text) {
        this.connection.writeBytes(text.getBytes(StandardCharsets.UTF_8));
    }

    public void close() {
        this.connection.close();
    }
}
