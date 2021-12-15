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
	
	protected abstract FSNode findRest(String rest);
	
	public FSNode find(String text) {
		int indexOfSlash = text.indexOf('/');
		
		if (indexOfSlash < 0) {
			if (name.equals(text) || ".".equals(text)) {
				return this;
			}
		} else {
			String firstPart = text.substring(0, indexOfSlash);
			String restPart = text.substring(indexOfSlash + 1);
			
			if (name.equals(text) || ".".equals(text)) {
				return findRest(restPart);
			} else if ("..".equals(name)) {
				getParent().find(restPart);
			}	
		}
		return null;
	}
}
