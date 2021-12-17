package nijakow.four.client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.net.SocketFactory;

public class ClientConnectionImpl implements ClientConnection {
	private String host;
	private int port;
	private SocketFactory socketFactory;
	private Socket socket;
	private ClientReceiveListener listener;
	
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
	public void establishConnection() throws IOException {
		socket = socketFactory.createSocket(host, port);
		InputStreamReader reader = new InputStreamReader(socket.getInputStream());
		int c =  reader.read();
		while (isConnected() && c != -1) {
			if (listener != null)
				listener.lineReceived(Character.toString((char) c));
			c = reader.read();
		}
	}
	
	@Override
	public void setClientReceiveListener(ClientReceiveListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void send(String message) throws IOException {
		socket.getOutputStream().write(message.getBytes());
	}
}