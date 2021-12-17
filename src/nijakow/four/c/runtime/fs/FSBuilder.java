package nijakow.four.c.runtime.fs;

import java.io.InputStream;
import java.util.Scanner;

public class FSBuilder {
	private final InputStream stream;
	
	private void processFile(Filesystem fs, Scanner scanner) {
		File file = null;
		StringBuilder text = new StringBuilder();
		
		while (scanner.hasNextLine()) {
			final String line = scanner.nextLine();
			if (line.startsWith("--- ")) {
				if (file != null) {
					file.setContents(text.toString());
					text = new StringBuilder();
				}
				String fname = line.substring(4).trim();
				file = fs.touchf(fname).asFile();
			} else {
				if (file != null) {
					text.append(line);
					text.append('\n');
				}
			}
		}
		
		if (file != null) {
			file.setContents(text.toString());
		}
	}
	
	public Filesystem build() {
		Filesystem fs = new Filesystem();
		processFile(fs, new Scanner(stream));
		return fs;
	}
	
	public FSBuilder(InputStream stream) {
		this.stream = stream;
	}
}
