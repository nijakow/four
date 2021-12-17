package nijakow.four.client.net;

public interface ClientConnection {
	public void close();
	public boolean isConnected();
	public void send(String message);
}