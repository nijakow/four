package nijakow.four.runtime;

import java.util.HashMap;
import java.util.Map;

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
	
	public FList keys() {
		return new FList(map.keySet().toArray(new Instance[0]));
	}
}
