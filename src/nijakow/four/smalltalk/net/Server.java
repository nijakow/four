package nijakow.four.smalltalk.net;

import nijakow.four.smalltalk.logging.LogLevel;
import nijakow.four.smalltalk.logging.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public class Server implements AutoCloseable {
	private final Logger logger;
	private final Selector selector;

	public Server(Logger logger) throws IOException {
		this.logger = logger;
		this.selector = Selector.open();
	}
	
	public void serveOn(String hostname, int port, Consumer<IConnection> consumer) throws IOException {
		logger.println(LogLevel.INFO, "Serving on " + hostname + " " + port + ".");
		ServerSocketChannel serverSocket = ServerSocketChannel.open();
		serverSocket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		serverSocket.bind(new InetSocketAddress(hostname, port));
		serverSocket.configureBlocking(false);
		SelectionKey key = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
		key.attach(consumer);
	}

	public IConnection connectTo(String hostname, int port) throws IOException {
		logger.println(LogLevel.INFO, "Connecting to " + hostname + " " + port + ".");
		SocketChannel socket = SocketChannel.open();
		socket.connect(new InetSocketAddress(hostname, port));
		socket.configureBlocking(false);
		RawConnection connection = new RawConnection(this.logger, socket);
		socket.register(selector, SelectionKey.OP_READ, connection);
		return connection;
	}
	
	public void tick(long timeout) throws IOException {
		if (timeout < 0) timeout = 0;
		else if (timeout == 0) timeout = 1;
		selector.select(timeout);
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> iter = selectedKeys.iterator();
		while (iter.hasNext()) {
			SelectionKey key = iter.next();
			if (key.isAcceptable()) {
				SocketChannel clientSocket = ((ServerSocketChannel) key.channel()).accept();
				clientSocket.configureBlocking(false);
				final Consumer<IConnection> onConnectFunc = (Consumer<IConnection>) key.attachment();
				if (onConnectFunc == null) {
					ByteBuffer bb = ByteBuffer.wrap("\r\n                      *** WHOOPS ***\r\n\r\nSorry, the server can't take any connections right now.\r\nPlease try again in a little while.\r\n\r\n".getBytes());
					clientSocket.write(bb);
					clientSocket.close();
				} else {
					RawConnection connection = new RawConnection(this.logger, clientSocket);
					clientSocket.register(selector, SelectionKey.OP_READ, connection);
					onConnectFunc.accept(connection);
				}
			} else if (key.isReadable()) {
				ByteBuffer bb = ByteBuffer.allocate(1024);
				int br;
				try {
					br = ((SocketChannel) key.channel()).read(bb);
				} catch (Exception e) {
					logger.printException(e);
					RawConnection attachment = (RawConnection) key.attachment();
					attachment.blockAllIO();
					key.cancel();
					key.channel().close();
					attachment.handleDisconnect();
					continue;
				}
				if (br <= 0) {
					key.cancel();
					((RawConnection) key.attachment()).handleDisconnect();
					key.channel().close();
				} else {
					int p;
					byte[] bytes = bb.array();
					for (p = 0; p < bytes.length; p++) {
						if (bytes[p] == 0)
							break;
						((RawConnection) key.attachment()).pushByte(bytes[p]);
					}
				}
			}
			iter.remove();
		}
	}

	@Override
	public void close() throws Exception {
		final Set<SelectionKey> keys = selector.keys();
		for (SelectionKey key : keys) {
			if (!key.isAcceptable()) {
				if (key.attachment() != null) {
					((RawConnection) key.attachment()).blockAllIO();
					((RawConnection) key.attachment()).close();
				}
			}
			key.channel().close();
			key.cancel();
		}
		selector.close();
	}
}
