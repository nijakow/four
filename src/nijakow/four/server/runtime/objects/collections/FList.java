package nijakow.four.server.runtime.objects.collections;

import nijakow.four.server.process.filedescriptor.IByteArray;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.FloatingInstance;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.objects.standard.FInteger;
import nijakow.four.server.runtime.types.ListType;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.server.runtime.exceptions.CastException;
import nijakow.four.server.storage.serialization.base.ISerializer;

import java.util.ArrayList;
import java.util.List;

public class FList extends FloatingInstance implements IByteArray {
	private final ListType type;
	private final List<Instance> list = new ArrayList<>();
	
	public FList(Instance... instances) throws CastException {
		this(Type.getList(), instances);
	}
	
	public FList(ListType type, Instance... instances) throws CastException {
		this.type = type;
		for (Instance i : instances) {
			type.getParent().expect(i);
			list.add(i);
		}
		registerToPool();
	}

	@Override
	public String getType() { return "list"; }

	@Override
	public FList asFList() {
		return this;
	}
	
	@Override
	public Instance index(Instance index) {
		return at(index.asInt());
	}
	
	@Override
	public Instance putIndex(Instance index, Instance value) throws FourRuntimeException {
		type.getParent().expect(value);
		try {
			list.set(index.asInt(), value);
		} catch (IndexOutOfBoundsException e) {
			throw new FourRuntimeException("Index " + index.asInt() + " out of bounds!");
		}
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
		if (i < 0) i = list.size() + i;
		return list.get(i);
	}
	
	public void insert(int i, Instance value) {
		if (i < 0) i = list.size() + i + 1;
		list.add(i, value);
	}

	public void append(Instance value) {
		list.add(value);
	}
	
	public Instance remove(int i) {
		return list.remove(i);
	}

	@Override
	public String getSerializationClassID() {
		return "list";
	}

	@Override
	public void serialize(ISerializer serializer) {
		ISerializer arraySerializer = serializer.openProperty("list.values");
		arraySerializer.writeInt(list.size());
		for (Instance i : list)
			arraySerializer.openEntry().writeObject(i).close();
		arraySerializer.close();
	}

    @Override
    public byte get(int index) {
        return (byte) at(index).asInt();
    }

    @Override
    public void put(int index, byte value) {
		FInteger fInteger = FInteger.get(value);
		list.set(index, fInteger);
    }

    @Override
    public int getLength() {
        return getSize();
    }
}
