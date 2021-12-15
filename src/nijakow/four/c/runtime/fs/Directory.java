package nijakow.four.c.runtime.fs;

import java.util.HashMap;
import java.util.Map;

public class Directory extends FSNode {
	private Map<String, FSNode> children = new HashMap<>();
	
	public Directory(FSNode parent, String name) {
		super(parent, name);
	}
	
	public Directory asDir() { return this; }

	protected FSNode findChild(String name) {
		return children.get(name);
	}
	
	public Directory mkdir(String name) {
		Directory dir = new Directory(this, name);
		children.put(name, dir);
		return dir;
	}
	
	public File touch(String name) {
		File file = new File(this, name);
		children.put(name, file);
		return file;
	}
}
