package nijakow.four.server;

import nijakow.four.server.net.Server;
import nijakow.four.server.runtime.objects.Blue;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.nvfs.NVFileSystem;
import nijakow.four.server.runtime.vm.VM;

import java.io.IOException;

public class Four implements Runnable {
	private final NVFileSystem fs;
	private final Server server;
	private final VM vm;
	private boolean wasStarted = false;

	public Four(NVFileSystem fs, int[] ports) throws IOException {
		this.fs = fs;
		this.server = new Server();
		this.vm = new VM(this.fs, this.server);
		
		for (int port : ports)
			server.serveOn(port);
	}
	
	public void start() throws FourRuntimeException {
		if (!wasStarted) {
			wasStarted = true;
			
			{
				Blue master = fs.getBlue("/secure/master.c");
				
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