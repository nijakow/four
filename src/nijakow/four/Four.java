package nijakow.four;

import java.io.IOException;

import nijakow.four.runtime.Blue;
import nijakow.four.runtime.Key;
import nijakow.four.runtime.fs.Filesystem;
import nijakow.four.runtime.fs.ResourceLoader;
import nijakow.four.runtime.vm.VM;
import nijakow.four.net.Server;

public class Four implements Runnable {
	private final Filesystem fs;
	private final Server server;
	private final VM vm;
	private boolean wasStarted = false;

	public Four(int[] ports) throws IOException {
		this.fs = new Filesystem(new ResourceLoader());
		this.server = new Server();
		this.vm = new VM(this.fs, this.server);
		
		for (int port : ports)
			server.serveOn(port);
	}
	
	public void start() {
		if (!wasStarted) {
			wasStarted = true;
			
			{
				Blue master = fs.getBlue("/secure/master.c");
				
				if (master == null) {
					throw new RuntimeException("/secure/master.c is not defined!");
				}
				
				vm.startFiber(master, Key.get("create"));
			}
		}
	}
	
	public void run() {
		start();
		
		while (true) {
			try {
				vm.tick();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("This error was not fatal. Execution will continue.");
				vm.reportError("exception", e.getClass().getName(), e.getMessage());
			}
			long wish = vm.notificationWish();
			if (wish > 0) {
				try {
					server.tick(wish);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
