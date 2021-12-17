package nijakow.four.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public class RawConnection implements IConnection {

	private final SocketChannel socket;
	private Consumer<String> inputHandler = null;
	
	
	public RawConnection(SocketChannel socket) {
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
		if (inputHandler != null) {
			/*
			 * TODO, FIXME, XXX: This is dangerous!
			 * The encoding might be corrupted.
			 * Better: Send the bytes directly to the receiver.
			 */
			inputHandler.accept(new String(bytes));
		}
	}
	
	public void handleDisconnect() {
		// TODO
	}


	@Override
	public void onInput(Consumer<String> consumer) {
		this.inputHandler = consumer;
	}

}
