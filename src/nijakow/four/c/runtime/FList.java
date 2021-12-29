package nijakow.four.c.runtime;

import java.util.ArrayList;
import java.util.List;

public class FList extends Instance {
	private final ListType type;
	private final List<Instance> list = new ArrayList<>();
	
	public FList(Instance... instances) {
		this(Type.getList(), instances);
	}
	
	public FList(ListType type, Instance... instances) {
		this.type = type;
		for (Instance i : instances) {
			type.getParent().expect(i);
			list.add(i);
		}
	}

	@Override
	public FList asFList() {
		return this;
	}
	
	@Override
	public Instance index(Instance index) {
		return at(index.asInt());
	}
	
	@Override
	public Instance putIndex(Instance index, Instance value) {
		type.getParent().expect(value);
		list.set(index.asInt(), value);
		return this;
	}

	@Override
	public int length() {
		return getSize();
	}

	public int getSize() {
		return list.size();
	}
	
	public Instance at(int i) {
		return list.get(i);
	}
	
	public void insert(int i, Instance value) {
		list.add(i, value);
	}
	
	public Instance remove(int i) {
		return list.remove(i);
	}
}
