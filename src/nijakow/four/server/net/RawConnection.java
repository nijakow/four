package nijakow.four.server.net;

import nijakow.four.server.logging.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class RawConnection implements IConnection {
	private final Logger logger;
	private final SocketChannel socket;
	private Consumer<String> inputHandler = null;
	private Runnable disconnectHandler = null;
	
	
	public RawConnection(Logger logger, SocketChannel socket) {
		this.logger = logger;
		this.socket = socket;
	}


	@Override
	public void writeBytes(byte[] bytes) {
		try {
			if (socket.isOpen())
				socket.write(ByteBuffer.wrap(bytes));
		} catch (IOException e) {
			logger.printException(e);
		}
	}
	
	@Override
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			logger.printException(e);
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
			inputHandler.accept(new String(bytes, StandardCharsets.UTF_8));
		}
	}
	
	public void handleDisconnect() {
		if (this.disconnectHandler != null) {
			disconnectHandler.run();
		}
	}


	@Override
	public void onInput(Consumer<String> consumer) {
		this.inputHandler = consumer;
	}

	@Override
	public void onDisconnect(Runnable runnable) {
		/*
		 * TODO: Immediately run it if the connection
		 *       has already been disconnected!
		 */
		this.disconnectHandler = runnable;
	}

	public void blockAllIO() {
		close();
	}
}
