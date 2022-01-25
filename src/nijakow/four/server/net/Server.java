package nijakow.four.server.net;

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

public class Server {
	private final Selector selector;
	private Consumer<IConnection> onConnectFunc = null;

	public Server() throws IOException {
		this.selector = Selector.open();
	}
	
	public void onConnect(Consumer<IConnection> onConnectFunc) {
		this.onConnectFunc = onConnectFunc;
	}
	
	public void serveOn(int port) throws IOException {
		ServerSocketChannel serverSocket = ServerSocketChannel.open();
		serverSocket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		serverSocket.bind(new InetSocketAddress("localhost", port));
		serverSocket.configureBlocking(false);
		serverSocket.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	public void tick(long timeout) throws IOException {
		selector.select(timeout);
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> iter = selectedKeys.iterator();
		while (iter.hasNext()) {
			SelectionKey key = iter.next();
			if (key.isAcceptable()) {
				SocketChannel clientSocket = ((ServerSocketChannel) key.channel()).accept();
				clientSocket.configureBlocking(false);
				if (onConnectFunc == null) {
					ByteBuffer bb = ByteBuffer.wrap("\r\n                      *** WHOOPS ***\r\n\r\nSorry, the server can't take any connections right now.\r\nPlease try again in a little while.\r\n\r\n".getBytes());
					clientSocket.write(bb);
					clientSocket.close();
				} else {
					RawConnection connection = new RawConnection(clientSocket);
					clientSocket.register(selector, SelectionKey.OP_READ, connection);
					onConnectFunc.accept(connection);
				}
			} else if (key.isReadable()) {
				ByteBuffer bb = ByteBuffer.allocate(1024);
				int br = ((SocketChannel) key.channel()).read(bb);
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
					}
					byte[] newbytes = new byte[p];
					while (p --> 0)
						newbytes[p] = bytes[p];
					((RawConnection) key.attachment()).handleInput(newbytes);
				}
			}
			iter.remove();
		}
	}
}