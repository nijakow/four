package nijakow.four;

import java.io.IOException;
import java.util.ArrayList;

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
		ArrayList<Integer> ports = new ArrayList<>();
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "--client":
				// TODO
				return;

			case "--port":
			case "-p":
				int j = 0;
				try {
					for (; j < args.length - i; j++) {
						ports.add(Integer.parseInt(args[++i]));
					}
				} catch (NumberFormatException | IndexOutOfBoundsException e) {
					if (j == 0)
						System.err.println("No proper port specified! Argument was: " + args[i]);
				}
				break;
				
			case "-h":
				System.out.println("--port");
				System.out.println("-p");
				System.out.println("         Specifies a (or multiple) port(s) to use");
				System.out.println("--client");
				System.out.println("         Starts a client instance");
				System.out.println("-h");
				System.out.println("         Prints this help");
				break;
			}
		}
		if (ports.isEmpty())
			ports.add(4242);
		int[] ps = new int[ports.size()];
		for (int i = 0; i < ps.length; i++)
			ps[i] = ports.get(i);
		Four four = new Four(ps);
		four.run();
	}
}
