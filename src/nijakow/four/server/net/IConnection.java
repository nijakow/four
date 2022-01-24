package nijakow.four.server.net;

import java.util.function.Consumer;

public interface IConnection {
	void writeBytes(byte[] bytes);
	void writeString(String string);
	void onInput(Consumer<String> consumer);
	void onDisconnect(Runnable runnable);
	void close();
}
