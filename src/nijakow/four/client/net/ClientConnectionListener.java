package nijakow.four.client.net;

public interface ClientConnectionListener {
	void connectionLost(ClientConnection connection);
	void charReceived(ClientConnection connection, char c);
}