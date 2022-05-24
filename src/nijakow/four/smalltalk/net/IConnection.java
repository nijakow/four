package nijakow.four.smalltalk.net;

import java.util.function.Consumer;

public interface IConnection {
	void writeBytes(byte[] bytes);
	void writeString(String string);
	void onInput(Consumer<String> consumer);
	void onSmalltalkInput(Consumer<String> consumer);
	void onEscape(Consumer<String[]> consumer);
	void onEscape(String escape, Consumer<String[]> consumer);
	void onDisconnect(Runnable runnable);
	void close();
	void pushByte(byte b);

	void writeEscaped(String... codes);
}
