package nijakow.four.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class FConnection implements Connection {

	private final SocketChannel socket;
	private final List<Byte> bytes = new ArrayList<>();
	
	
	public FConnection(SocketChannel socket) {
		this.socket = socket;
	}


	@Override
	public void writeBytes(byte[] bytes) {
		try {
			socket.write(ByteBuffer.wrap(bytes));
		} catch (IOException e) {
			// TODO: Handle error gracefully
			e.printStackTrace();
		}
	}


	@Override
	public void writeString(String string) {
		writeBytes(string.getBytes());
	}
	
	public void handleInput(byte[] bytes) {
		for (byte b : bytes)
			this.bytes.add(b);
		// TODO: Call the callback function
	}
	
	public void handleDisconnect() {
		// TODO
	}

}
