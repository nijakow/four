package nijakow.four.client.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import nijakow.four.client.PreferencesHelper;

public class ClientConnectionImpl implements ClientConnection {
	private PreferencesHelper prefs;
	private SocketFactory socketFactory;
	private Socket socket;
	
	public ClientConnectionImpl(PreferencesHelper prefs, boolean connect) {
		this.prefs = prefs;
		socketFactory = SocketFactory.getDefault();
		if (connect) {
			try {
				establishConnection();
			} catch (UnknownHostException e) {
				System.err.println("Could not connect to host: " + e.getLocalizedMessage());
			}
		}
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
	
	public void establishConnection() throws UnknownHostException {
		try {
			socket = socketFactory.createSocket(prefs.getHostname(), prefs.getPort());
		}catch (IOException e) {
			System.err.println("Could not connect to host: " + e.getLocalizedMessage());
			e.printStackTrace();
			System.err.println("----------------------------");
		}
	}

	
	@Override
	public void send(String message) {
		// TODO Send a message
	}
}