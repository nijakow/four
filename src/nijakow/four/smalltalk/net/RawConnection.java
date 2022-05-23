package nijakow.four.smalltalk.net;

import nijakow.four.smalltalk.logging.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.function.Consumer;

public class RawConnection implements IConnection {
	private final Logger logger;
	private final SocketChannel socket;
	private Consumer<String> inputHandler = null;
	private Consumer<String> smalltalkHandler = null;
	private Consumer<String[]> escapeHandler = null;
	private Runnable disconnectHandler = null;
	private final ArrayList<Byte> currentLine = new ArrayList<>();
	private final ArrayList<Byte> currentEscaped = new ArrayList<>();
	private boolean isEscaped = false;
	
	
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
			blockAllIO();
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
	public void pushByte(byte b) {
		if (isEscaped) {
			if (b == 0x03) {
				isEscaped = false;
				byte[] elements = new byte[currentEscaped.size()];
				for (int i = 0; i < elements.length; i++)
					elements[i] = currentEscaped.get(i);
				final String escaped = new String(elements, StandardCharsets.UTF_8);
				String[] split = escaped.split(":", -1);
				for (int index = 1; index < split.length; index++)
					split[index] = new String(Base64.getDecoder().decode(split[index]), StandardCharsets.UTF_8);
				currentEscaped.clear();
				if (split.length > 0) {
					if ("line/smalltalk".equals(split[0])) {
						if (smalltalkHandler != null && split.length >= 2) {
							/*
							 * Smalltalk handlers will be reset!
							 */
							Consumer<String> consumer = smalltalkHandler;
							smalltalkHandler = null;
							consumer.accept(split[1]);
						}
					} else {
						if (escapeHandler != null)
							escapeHandler.accept(split);
					}
				}
			} else {
				currentEscaped.add(b);
			}
		} else {
			if (b == 0x02) {
				isEscaped = true;
				return;
			} else if (b == '\n') {
				byte[] elements = new byte[currentLine.size()];
				for (int i = 0; i < elements.length; i++)
					elements[i] = currentLine.get(i);
				currentLine.clear();
				if (inputHandler != null)
					inputHandler.accept(new String(elements, StandardCharsets.UTF_8));
			} else {
				currentLine.add(b);
			}
		}
	}


	@Override
	public void writeString(String string) {
		writeBytes(string.getBytes());
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
	public void onSmalltalkInput(Consumer<String> consumer) {
		this.smalltalkHandler = consumer;
	}

	@Override
	public void onEscape(Consumer<String[]> consumer) {
		this.escapeHandler = consumer;
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
