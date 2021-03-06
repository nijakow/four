package nijakow.four.client.net;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.net.SocketFactory;

public class ClientConnectionImpl implements ClientConnection {
	private final String host;
	private final int port;
	private final SocketFactory socketFactory;
	private Socket socket;
	private ClientConnectionListener listener;
	
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
	
	@Override
	public void openStreams() throws IOException {
		InputStreamReader reader = new InputStreamReader(socket.getInputStream());
		int c =  reader.read();
		while (isConnected() && c != -1) {
			if (listener != null)
				listener.charReceived(this, (char) c);
			c = reader.read();
		}
		if (listener != null)
			listener.connectionLost(this);
	}
	
	@Override
	public void establishConnection() throws IOException {
		socket = socketFactory.createSocket(host, port);
	}
	
	public void setClientConnectionListener(ClientConnectionListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void send(String message) throws IOException {
		socket.getOutputStream().write(message.getBytes());
	}
}