package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.net.IConnection;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class STPort extends STInstance {
    private final IConnection connection;
    private final Map<String, Consumer<String>> keys = new HashMap<>();

    public STPort(IConnection connection) {
        this.connection = connection;
        this.connection.onEscape((strings) -> processEscaped(strings));
        // TODO: this.connection.onDisconnect();
    }

    private String generateKey() {
        return "" + Math.random();  // TODO: Generate better keys!
    }

    @Override
    public STClass getClass(World world) {
        return world.getPortClass();
    }

    public void onInput(Consumer<STString> callback) {
        this.connection.onInput((line) -> callback.accept(new STString(line)));
    }

    public void write(String text) {
        this.connection.writeString(text);
    }

    public void writeEscaped(String code, String... args) {
        this.connection.writeString("\02");
        this.connection.writeString(code);
        for (int i = 0; i < args.length; i++) {
            this.connection.writeString(":");
            this.connection.writeString(Base64.getEncoder().encodeToString(args[i].getBytes(StandardCharsets.UTF_8)));
        }
        this.connection.writeString("\03");
    }

    public void prompt(String prompt) {
        writeEscaped("prompt/plain", prompt);
    }

    public void password(String prompt) {
        writeEscaped("prompt/password", prompt);
    }

    public void edit(String title, String text, Consumer<STString> consumer) {
        final String key = generateKey();
        keys.put(key, (string) -> {
            keys.remove(key);
            consumer.accept(string == null ? null : new STString(string));
        });
        writeEscaped("editor/edit", key, title, text);
    }

    public void uploadToUser(String text) {
        writeEscaped("file/upload", text);
    }

    public void downloadFromUser(Consumer<STString> consumer) {
        final String key = generateKey();
        keys.put(key, (string) -> {
            keys.remove(key);
            consumer.accept(string == null ? null : new STString(string));
        });
        writeEscaped("file/download", key);
    }

    private void processEscaped(String[] message) {
        switch (message[0])
        {
            case "editor/saved":
            case "file/upload":
                if (keys.containsKey(message[1]))
                    keys.get(message[1]).accept(message[2]);
                break;
            case "editor/cancelled":
                if (keys.containsKey(message[1]))
                    keys.get(message[1]).accept(null);
                break;
            default:
                System.err.println(message[0]);
                // TODO: Warning
                break;
        }
    }

    public void close() {
        this.connection.close();
    }
}
