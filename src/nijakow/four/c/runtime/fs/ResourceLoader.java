package nijakow.four.c.runtime.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import nijakow.four.Four;

public class ResourceLoader {
	public String getResourceText(String path) {
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(Filesystem.WORKING_DIR + path))) {
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
		return new File(Filesystem.WORKING_DIR + path).listFiles();
	}
}
