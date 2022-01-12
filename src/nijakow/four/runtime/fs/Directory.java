package nijakow.four.runtime.fs;

import java.util.*;

public class Directory extends FSNode {
	private final Map<String, FSNode> children = new HashMap<>();
	
	public Directory(Filesystem fs, FSNode parent, String name) {
		super(fs, parent, name);
	}
	
	public Directory asDir() { return this; }

	protected FSNode findChild(String name) {
		return children.get(name);
	}
	
	public Collection<FSNode> getChildren() { return children.values(); }

	protected boolean contains(FSNode node) {
		if (children.containsValue(node)) {
			return true;
		} else {
			boolean found = false;
			for (FSNode child : getChildren()) {
				if (child instanceof Directory) {
					found = child.asDir().contains(node);
				} else if (child.getFullName().equals(node.getFullName())) {
					found = true;
				}
				if (found) {
					break;
				}
			}
			return found;
		}
	}

	protected boolean containsFile(File file) {
		if (children.containsValue(file)) {
			return true;
		} else {
			boolean found = false;
			for (FSNode node : getChildren()) {
				if (node instanceof Directory) {
					found = node.asDir().containsFile(file);
				} else if (node.getFullName().equals(file.getFullName())) {
					found = true;
				}
				if (found) {
					break;
				}
			}
			return found;
		}
	}

	protected List<File> getAllFiles() {
		List<File> list = new LinkedList<>();
		for (FSNode node : getChildren()) {
			if (node instanceof Directory) {
				list.addAll(node.asDir().getAllFiles());
			} else {
				list.add(node.asFile());
			}
		}
		return list;
	}

	public Directory mkdir(String name) throws ImmutableException {
		Directory dir = new Directory(getFilesystem(), this, name);
		children.put(name, dir);
		return dir;
	}

	protected void insertNode(FSNode node) {
		children.put(node.getName(), node);
	}

	public File touch(String name) throws ImmutableException {
		File file = new File(getFilesystem(), this, name);
		children.put(name, file);
		return file;
	}
}
