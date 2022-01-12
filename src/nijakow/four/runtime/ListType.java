package nijakow.four.runtime;

public class ListType extends Type {
	private final Type parent;
	
	public ListType(Type type) {
		this.parent = type;
	}
	
	public Type getParent() { return parent; }

	@Override
	public Instance cast(Instance instance) {
		if (check(instance))
			return instance;
		else
			throw new RuntimeException("Can't cast!");
	}
	
	@Override
	public boolean check(Instance instance) {
		return (instance instanceof FList) || instance.isNil();
	}
}
