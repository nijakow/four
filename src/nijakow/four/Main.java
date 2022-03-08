package nijakow.four;

import nijakow.four.server.users.IdentityDatabase;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.client.ClientWindow;
import nijakow.four.server.Four;
import nijakow.four.server.nvfs.NVFileSystem;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws IOException, CompilationException, ParseException {
		IdentityDatabase db = new IdentityDatabase();
		NVFileSystem fileSystem = new NVFileSystem(db);

		String hostname = null;
		ArrayList<Integer> ports = new ArrayList<>();

		boolean server = false;
		boolean guest = false;
		int clients = 0;
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-d":
			case "--directory":
				fileSystem.load(new java.io.File(args[++i]), db);
				break;

			case "--client":
			case "-c":
				clients++;
				break;

			case "--server":
			case "-s":
				server = true;
				break;

			case "-b":
			case "--bind":
			case "--host":
			case "--hostname":
				hostname = args[++i];
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

			case "--root-password":
				db.getRootUser().setPassword(args[++i]);
				break;

			case "--enable-guest":
				guest = true;
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
		if (!server && clients == 0)
			clients = 1;
		while (clients --> 0)
			ClientWindow.openWindow(hostname, ps);
		if (server) {
			Four four = new Four(db, fileSystem, hostname == null ? "localhost" : hostname, ps);
			if (guest) {
				db.newUser("guest").setPassword("42");
			}
			four.run();
		}
	}
}
