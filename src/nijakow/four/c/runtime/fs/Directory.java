package nijakow.four.c.runtime.fs;

import java.util.HashMap;
import java.util.Map;

public class Directory extends FSNode {
	private final Map<String, FSNode> children = new HashMap<>();
	
	public Directory(Filesystem fs, FSNode parent, String name) {
		super(fs, parent, name);
	}
	
	public Directory asDir() { return this; }

	protected FSNode findChild(String name) {
		return children.get(name);
	}
	
	public Directory mkdir(String name) {
		Directory dir = new Directory(getFilesystem(), this, name);
		children.put(name, dir);
		return dir;
	}

	protected void insertNode(FSNode node) {
		children.put(node.getName(), node);
	}

	public File touch(String name) {
		File file = new File(getFilesystem(), this, name);
		children.put(name, file);
		return file;
	}
}
