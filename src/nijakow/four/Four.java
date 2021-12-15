package nijakow.four;

import java.io.IOException;

import nijakow.four.c.runtime.fs.Filesystem;
import nijakow.four.c.runtime.vm.VM;
import nijakow.four.net.Server;

public class Four implements Runnable {
	private final Filesystem fs;
	private final Server server;
	private final VM vm;
	
	public Four(int[] ports) throws IOException {
		this.fs = new Filesystem();
		this.server = new Server();
		this.vm = new VM();
		
		for (int port : ports)
			server.serveOn(port);
	}
	
	public void run() {
		while (true) {
			vm.tick();
			try {
				server.tick();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		Four four = new Four(new int[] { 8888 });
		four.run();
	}
}
