package nijakow.four.c.runtime.fs;

public abstract class FSNode {
	private FSNode parent;
	private String name;
	
	public FSNode(FSNode parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	public FSNode getParent() {
		return parent;
	}
	
	public FSNode getRoot() {
		FSNode node;
		for (node = this; node.getParent() != null; node = node.getParent());
		return node;
	}
	
	protected abstract FSNode findChild(String name);
	
	private FSNode find1(String name) {
		if (name.isEmpty() || ".".equals(name)) {
			return this;
		} else if ("..".equals(name)) {
			return getParent();
		} else {
			return findChild(name);
		}
	}
	
	private FSNode findN(String text) {
		int indexOfSlash = text.indexOf('/');
		
		if (indexOfSlash < 0) {
			return find1(text);
		} else {
			String firstPart = text.substring(0, indexOfSlash);
			String restPart = text.substring(indexOfSlash + 1);
			
			FSNode child = find1(firstPart);
			
			if (child != null)
				return child.findN(restPart);	
		}
		return null;
	}
	
	public FSNode find(String text) {
		if (text.startsWith("/")) {
			return getRoot().findN(text);
		} else {
			return findN(text);
		}
	}
}
