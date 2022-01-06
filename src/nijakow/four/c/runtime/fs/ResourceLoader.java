package nijakow.four.c.runtime.fs;

import java.io.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import nijakow.four.Four;

public class ResourceLoader {
	/*public String getResourceText(String path) {
		try {
			if (!path.startsWith("/")) path = "/" + path;
			InputStream stream = Four.class.getResourceAsStream("lib" + path);
			InputStreamReader reader = new InputStreamReader(stream);
			StringBuffer buffer = new StringBuffer();
			int c;
			while ((c = reader.read()) != -1)
				buffer.append((char) c);
			return buffer.toString();
		} catch (Exception e) {
			return null;
		}
	}*/

	public String getResourceText(String path) {
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(Four.WORKING_DIR + path))) {
			StringBuilder builder = new StringBuilder();
			int c;
			while ((c = reader.read()) != -1) {
				builder.append((char) c);
			}
			return builder.toString();
		} catch (IOException e) {
			return null;
		}
	}

	public File[] listFolderContents(String path) {
		return new File(Four.WORKING_DIR + path).listFiles();
	}
}
