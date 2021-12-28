package nijakow.four.client.net;

public interface ClientConnectionListener {
	public void connectionLost(ClientConnection connection);
	public void charReceived(ClientConnection connection, char c);
}