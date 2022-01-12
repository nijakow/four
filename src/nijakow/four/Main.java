package nijakow.four;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import nijakow.four.runtime.fs.Filesystem;
import nijakow.four.client.ClientWindow;

public class Main {

	public static void main(String[] args) throws IOException {
		ArrayList<Integer> ports = new ArrayList<>();
		boolean server = false;
		int clients = 0;
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-d":
			case "--directory":
				Filesystem.WORKING_DIR = args[++i];
				if (!Filesystem.WORKING_DIR.endsWith(File.separator))
					Filesystem.WORKING_DIR += File.separator;
				Filesystem.WORKING_DIR += Filesystem.LIB_FOLDER_NAME;
				break;

			case "--client":
			case "-c":
				clients++;
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
			
			case "--license":
			case "--info":
				System.out.println("");
				System.out.println("This is the MUD driver Four by nijakow.");
				System.out.println("This software is licensed under the EUPL v1.2.");
				System.out.println("");
				System.out.println("The Licensor will in no event be liable for any direct or indirect,");
				System.out.println("material or moral, damages of any kind, arising out of the Licence or of the use");
				System.out.println("of the Work, including without limitation, damages for loss of goodwill, work");
				System.out.println("stoppage, computer failure or malfunction, loss of data or any commercial");
				System.out.println("damage, even if the Licensor has been advised of the possibility of such damage.");
				System.out.println("");
				System.out.println("You can find more information here:");
				System.out.println(" - https://github.com/nijakow/four");
				System.out.println(" - https://joinup.ec.europa.eu/collection/eupl");
				System.out.println("");
				break;
				
			case "-h":
			default:
				System.out.println("--directory");
				System.out.println("-d");
				System.out.println("         Specifies the path to the resource directory with the lib folder");
				System.out.println();
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
				System.out.println();
				System.out.println("--license");
				System.out.println("--info");
				System.out.println("         Prints license and general information");
				break;
			}
		}
		if (ports.isEmpty())
			ports.add(4242);
		int[] ps = new int[ports.size()];
		for (int i = 0; i < ps.length; i++)
			ps[i] = ports.get(i);
		while (clients --> 0)
			ClientWindow.openWindow(ps);
		if (server) {
			Four four = new Four(ps);
			four.run();
		}
	}
}
