package nijakow.four.server;

import nijakow.four.server.logging.Logger;
import nijakow.four.server.net.Server;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.nvfs.NVFileSystem;
import nijakow.four.server.runtime.security.users.IdentityDatabase;
import nijakow.four.server.runtime.vm.VM;

import java.io.IOException;

public class Four implements Runnable {
	private final Logger logger;
	private final IdentityDatabase db;
	private final NVFileSystem fs;
	private final Server server;
	private final VM vm;
	private boolean wasStarted = false;

	public Four(IdentityDatabase db, NVFileSystem fs, String hostname, int[] ports) throws IOException {
		this.logger = new Logger();
		this.db = db;
		this.fs = fs;
		this.server = new Server();
		this.vm = new VM(this.logger, this.db, this.fs, this.server);
		
		for (int port : ports)
			server.serveOn(hostname, port);
	}
	
	public void start() throws FourRuntimeException {
		if (!wasStarted) {
			wasStarted = true;
			
			{
				Blue master = fs.getBlue(this.vm,"/secure/master.c");
				
				if (master == null) {
					throw new FourRuntimeException("/secure/master.c is not defined!");
				}
				
				vm.startFiber(master, Key.get("create"));
			}
		}
	}
	
	public void run() {
		try {
			start();
		} catch (FourRuntimeException e) {
			e.printStackTrace();
			System.err.println("Fatal error! Stopping...");
			return;
		}

		while (true) {
			try {
				vm.tick();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("This error was not fatal. Execution will continue.");
				try {
					vm.reportError("exception", e.getClass().getName(), e.getMessage());
				} catch (FourRuntimeException ex) {
					ex.printStackTrace();
				}
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
