package nijakow.four.c.runtime.fs;

import java.io.InputStream;
import java.io.InputStreamReader;

import nijakow.four.Four;

public class ResourceLoader {
	public String getResourceText(String path) {
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
	}
}
