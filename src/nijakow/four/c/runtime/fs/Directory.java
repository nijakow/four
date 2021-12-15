package nijakow.four.c.runtime.fs;

import java.util.HashMap;
import java.util.Map;

public class Directory extends FSNode {
	private Map<String, FSNode> children = new HashMap<>();
	
	public Directory(FSNode parent, String name) {
		super(parent, name);
	}

	protected FSNode findRest(String rest) {
		for (FSNode child : children.values()) {
			FSNode n = child.find(rest);
			if (n != null)
				return n;
		}
		return null;
	}
	
	public Directory createDir(String name) {
		Directory dir = new Directory(this, name);
		children.put(name, dir);
		return dir;
	}
	
	public File createFile(String name) {
		File file = new File(this, name);
		children.put(name, file);
		return file;
	}
}
