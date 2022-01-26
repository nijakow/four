package nijakow.four.server.runtime.types;

import nijakow.four.server.runtime.exceptions.CastException;
import nijakow.four.server.runtime.objects.collections.FList;
import nijakow.four.server.runtime.objects.Instance;

public class ListType extends Type {
	private final Type parent;
	
	public ListType(Type type) {
		this.parent = type;
	}

	public String getName() { return "list of " + parent.getName(); }

	public Type getParent() { return parent; }

	@Override
	public Instance cast(Instance instance) throws CastException {
		if (check(instance))
			return instance;
		else
			throw new CastException(this, instance);
	}
	
	@Override
	public boolean check(Instance instance) {
		return (instance instanceof FList) || instance.isNil();
	}
}
