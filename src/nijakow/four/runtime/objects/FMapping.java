package nijakow.four.runtime.objects;

import nijakow.four.serialization.base.ISerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FMapping extends Instance {
	private final Map<Instance, Instance> map = new HashMap<>();
	
	public FMapping(Instance... instances) {
		boolean isKey = true;
		Instance key = null;
		for (Instance i : instances) {
			if (isKey) {
				key = i;
			} else {
				map.put(key, i);
			}
			isKey = !isKey;
		}
	}

	@Override
	public FMapping asFMapping() {
		return this;
	}
	
	@Override
	public Instance index(Instance index) {
		return map.getOrDefault(index, Instance.getNil());
	}
	
	@Override
	public Instance putIndex(Instance index, Instance value) {
		if (index.isNil()) {
			map.remove(index);
		} else {
			map.put(index, value);
		}
		return this;
	}

	@Override
	public String getSerializationClassID() {
		return "mapping";
	}

	@Override
	public void serialize(ISerializer serializer) {
		ISerializer arraySerializer = serializer.openEntry();
		Set<Instance> keys = map.keySet();
		arraySerializer.writeInt(keys.size());
		for (Instance i : keys)
			arraySerializer.openEntry().writeObject(i).writeObject(map.get(i)).close();
		arraySerializer.close();
	}
}
