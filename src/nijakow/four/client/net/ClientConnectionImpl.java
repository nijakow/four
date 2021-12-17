package nijakow.four.client.net;

import java.io.IOException;
import java.net.Socket;

import javax.net.SocketFactory;

public class ClientConnectionImpl implements ClientConnection {
	private String host;
	private int port;
	private SocketFactory socketFactory;
	private Socket socket;
	
	protected ClientConnectionImpl(String host, int port) {
		this.port = port;
		this.host = host;
		socketFactory = SocketFactory.getDefault();
	}
	
	@Override
	public boolean isConnected() {
		return socket != null && !socket.isClosed() && socket.isConnected() && socket.isBound();
	}
	
	@Override
	public void close() {
		try {
			socket.close();
		} catch (NullPointerException e) {
			System.err.println("Nothing to close");
		} catch (IOException e) {
			System.err.println("Could not close connection: " + e.getLocalizedMessage());
			e.printStackTrace();
			System.err.println("----------------------------");
		}
	}
	
	public void establishConnection() throws IOException {
		socket = socketFactory.createSocket(host, port);
	}

	
	@Override
	public void send(String message) {
		// TODO Send a message
	}
}