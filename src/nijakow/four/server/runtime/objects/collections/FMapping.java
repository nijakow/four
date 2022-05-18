package nijakow.four.server.runtime.objects.collections;

import nijakow.four.server.runtime.objects.FloatingInstance;
import nijakow.four.server.runtime.objects.Instance;

import java.util.HashMap;
import java.util.Map;

public class FMapping extends FloatingInstance {
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
		registerToPool();
	}

	@Override
	public String getType() { return "mapping"; }

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
}
