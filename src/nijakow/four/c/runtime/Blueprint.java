package nijakow.four.c.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blueprint {
	private List<Key> slots = new ArrayList<>();
	private Map<Key, Code> methods = new HashMap<>();

	public Integer getSlotIndex(Key key) {
		return slots.indexOf(key);
	}

	public Code getMethod(Key key) {
		return methods.get(key);
	}

	public void addSlot(Type type, Key name) {
		if (!slots.contains(name))
			slots.add(name);
	}
	
	public void addMethod(Key name, Code code) {
		methods.put(name, code);
	}

	public Blue createBlue() {
		return new Blue(this, slots.size());
	}

}
