package nijakow.four.net;

import java.util.function.Consumer;

public interface IConnection {
	void writeBytes(byte[] bytes);
	void writeString(String string);
	void onInput(Consumer<String> consumer);
}
