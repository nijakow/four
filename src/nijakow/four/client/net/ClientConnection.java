package nijakow.four.client.net;

import java.io.IOException;

public interface ClientConnection {
	void close();
	boolean isConnected();
	void establishConnection() throws IOException;
	void send(String message) throws IOException;
	void setClientConnectionListener(ClientConnectionListener listener);
	void openStreams() throws IOException;
	
	static ClientConnection getClientConnection(String host, int port) {
		return new ClientConnectionImpl(host, port);
	}
}