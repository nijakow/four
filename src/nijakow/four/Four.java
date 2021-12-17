package nijakow.four;

import java.io.IOException;
import java.util.ArrayList;

import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.fs.FSBuilder;
import nijakow.four.c.runtime.fs.FSNode;
import nijakow.four.c.runtime.fs.Filesystem;
import nijakow.four.c.runtime.vm.VM;
import nijakow.four.client.ClientWindow;
import nijakow.four.net.Server;

public class Four implements Runnable {
	private final Filesystem fs;
	private final Server server;
	private final VM vm;
	private boolean wasStarted = false;
	
	public Four(int[] ports) throws IOException {
		this.fs = new FSBuilder(this.getClass().getResourceAsStream("mudlib.txt")).build();
		this.server = new Server();
		this.vm = new VM(this.fs, this.server);
		
		for (int port : ports)
			server.serveOn(port);
	}
	
	public void start() {
		if (!wasStarted) {
			wasStarted = true;
			
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
