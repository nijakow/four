package nijakow.four.client.net;

public interface ClientConnection {
	public void close();
	public boolean isConnected();
	public void send(String message);
	
	public static ClientConnection getClientConnection(String host, int port) {
		return new ClientConnectionImpl(host, port);
	}
}