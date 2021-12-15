package nijakow.four.net;

public interface Connection {
	void writeBytes(byte[] bytes);
	void writeString(String string);
}
