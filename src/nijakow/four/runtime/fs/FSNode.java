package nijakow.four.runtime.fs;

public abstract class FSNode {
	private final Filesystem fs;
	private final FSNode parent;
	private final String name;
	
	public FSNode(Filesystem fs, FSNode parent, String name) {
		this.fs = fs;
		this.parent = parent;
		this.name = name;
	}
	
	public File asFile() { return null; }
	public Directory asDir() { return null; }

	public Filesystem getFilesystem() { return fs; }
		
	public FSNode getParent() { return parent; }
	public String getName() { return name; }
	
	public String getFullName() {
		if (getParent() == null) {
			return "";
		} else {
			return (getParent().getFullName()) + "/" + getName();
		}
	}
	
	public FSNode getRoot() {
		FSNode node;
		for (node = this; node.getParent() != null; node = node.getParent());
		return node;
	}
	
	protected abstract FSNode findChild(String name);
	
	private FSNode find1(String name, boolean create, boolean isDir) throws ImmutableException {
		if (name.isEmpty() || ".".equals(name)) {
			return this;
		} else if ("..".equals(name)) {
			return getParent();
		} else {
			FSNode node = findChild(name);
			if (node == null) {
				if (create) {
					node = isDir ? asDir().mkdir(name) : asDir().touch(name);
				} else {
					return null;
				}
			}
			return node;
		}
	}
	
	private FSNode findN(String text, boolean create) throws ImmutableException {
		int indexOfSlash = text.indexOf('/');
		
		if (indexOfSlash < 0) {
			return find1(text, create, false);
		} else {
			String firstPart = text.substring(0, indexOfSlash);
			String restPart = text.substring(indexOfSlash + 1);
			
			FSNode child = find1(firstPart, create, true);
			
			if (child != null)
				return child.findN(restPart, create);
		}
		return null;
	}
	
	public FSNode find(String text, boolean create) throws ImmutableException {
		if (text.startsWith("/")) {
			return getRoot().findN(text, create);
		} else {
			return findN(text, create);
		}
	}
	
	public FSNode find(String text) {
		try {
			return find(text, false);
		} catch (ImmutableException e) {
			return null;
		}
	}
}