package nijakow.four;

import nijakow.four.launcher.FLauncherWindow;
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
		String storage = "/tmp/four";

		String hostname = null;
		ArrayList<Integer> ports = new ArrayList<>();

		boolean server = false;
		boolean guest = false;
		int clients = 0;
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-d":
			case "--directory":
			case "--lib":
			case "--library":
				fileSystem.load(new java.io.File(args[++i]), db);
				break;

			case "--storage":
				storage = args[++i];
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
				int j = i;
				try {
					for (j = i; j < args.length; j++) {
						ports.add(Integer.parseInt(args[j + 1]));
					}
				} catch (NumberFormatException | IndexOutOfBoundsException e) {
					if (ports.isEmpty())
						System.err.println("No proper port specified! Argument was: " + args[i]);
				}
				i = j;
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
		if (!server && clients == 0) {
			FLauncherWindow.showWindow(ps, guest, hostname, storage, db);
		}
		while (clients --> 0)
			ClientWindow.openWindow(hostname, ps);
		if (server) {
			Four four = new Four(db, fileSystem, storage, hostname == null ? "localhost" : hostname, ps);
			if (guest) {
				db.newUser("guest").setPassword("42");
			}
			four.run();
		}
	}
}
