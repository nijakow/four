package nijakow.four.net;

public interface IConnection {
	void writeBytes(byte[] bytes);
	void writeString(String string);
}
