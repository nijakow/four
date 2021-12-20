package nijakow.four.client.net;

import java.io.IOException;

public interface ClientConnection {
	public void close();
	public boolean isConnected();
	public void establishConnection() throws IOException;
	public void send(String message) throws IOException;
	public void setClientReceiveListener(ClientReceiveListener listener);
	public void openStreams() throws IOException;
	
	public static ClientConnection getClientConnection(String host, int port) {
		return new ClientConnectionImpl(host, port);
	}
}