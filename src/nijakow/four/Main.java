package nijakow.four;

import java.io.IOException;
import java.util.ArrayList;

import nijakow.four.client.ClientWindow;

public class Main {

	public static void main(String[] args) throws IOException {
		ArrayList<Integer> ports = new ArrayList<>();
		boolean server = false, client = false;
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "--client":
			case "-c":
				client = true;
				break;

			case "--server":
			case "-s":
				server = true;
				break;
				
			case "--port":
			case "-p":
				int j = 0;
				try {
					for (; j < args.length - i; j++) {
						ports.add(Integer.parseInt(args[i + 1]));
						i++;
					}
				} catch (NumberFormatException | IndexOutOfBoundsException e) {
					if (j == 0)
						System.err.println("No proper port specified! Argument was: " + args[i]);
				}
				break;
				
			case "-h":
			default:
				System.out.println("--port");
				System.out.println("-p");
				System.out.println("         Specifies a (or multiple) port(s) to use");
				System.out.println();
				System.out.println("--client");
				System.out.println("-c");
				System.out.println("         Starts a client instance");
				System.out.println();
				System.out.println("--server");
				System.out.println("-s");
				System.out.println("         Starts a server instance");
				System.out.println();
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
		if (client)
			ClientWindow.openWindow(ps);
		if (server) {
			Four four = new Four(ps);
			four.run();
		}
	}
}
