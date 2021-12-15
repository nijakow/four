package nijakow.four;

import java.io.IOException;

import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.fs.FSNode;
import nijakow.four.c.runtime.fs.Filesystem;
import nijakow.four.c.runtime.vm.VM;
import nijakow.four.net.Server;

public class Four implements Runnable {
	private final Filesystem fs;
	private final Server server;
	private final VM vm;
	private boolean wasStarted = false;
	
	public Four(int[] ports) throws IOException {
		this.fs = new Filesystem();
		this.server = new Server();
		this.vm = new VM();
		
		for (int port : ports)
			server.serveOn(port);
	}
	
	public void start() {
		if (!wasStarted) {
			wasStarted = true;
			
			fs.initialize();
			
			{
				FSNode f = fs.find("/secure/master.c");
				
				if (f == null || f.asFile() == null) {
					throw new RuntimeException("/secure/master.c is not defined!");
				}
				
				vm.startFiber(f.asFile().getInstance(), Key.get("create"));
			}
		}
	}
	
	public void run() {
		start();
		
		while (true) {
			vm.tick();
			double wish = vm.notificationWish();
			if (wish > 0) {
				try {
						server.tick((long) (vm.notificationWish() * 1000));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		Four four = new Four(new int[] { 8888 });
		four.run();
	}
}
