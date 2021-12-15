package nijakow.four.net;

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

public class Server {
	private final Selector selector;

	public Server() throws IOException {
		this.selector = Selector.open();
	}
	
	public void serveOn(int port) throws IOException {
		ServerSocketChannel serverSocket = ServerSocketChannel.open();
		serverSocket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		serverSocket.bind(new InetSocketAddress("localhost", port));
		serverSocket.configureBlocking(false);
		serverSocket.register(selector, SelectionKey.OP_ACCEPT);
	}
	
	public void tick() throws IOException {
		selector.select();
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		Iterator<SelectionKey> iter = selectedKeys.iterator();
		while (iter.hasNext()) {
			SelectionKey key = iter.next();
			if (key.isAcceptable()) {
				SocketChannel clientSocket = ((ServerSocketChannel) key.channel()).accept();
				clientSocket.configureBlocking(false);
				clientSocket.register(selector, SelectionKey.OP_READ);
				// TODO: Create connection object and call callback with it
			} else if (key.isReadable()) {
				ByteBuffer bb = ByteBuffer.allocate(1024);
				int br = ((SocketChannel) key.channel()).read(bb);
				if (br <= 0) {
					key.cancel();
					key.channel().close();
					// TODO: Close connection object
				}
				System.out.println(new String(bb.array()));
			}
			iter.remove();
		}
	}
}
