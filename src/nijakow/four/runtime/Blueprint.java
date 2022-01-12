package nijakow.four.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nijakow.four.util.Pair;

public class Blueprint {
	private final String filename;
	private final List<Blueprint> supers = new ArrayList<>();
	private final List<Pair<Type, Key>> slots = new ArrayList<>();
	private final Map<Key, Code> methods = new HashMap<>();

	public Blueprint(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String toString() {
		return filename;
	}

	public Code getMethod(Key key) {
		Code c = methods.get(key);
		if (c == null) {
			for (Blueprint s : supers) {
				c = s.getMethod(key);
				if (c != null)
					break;
			}
		}
		return c;
	}

	public void addSuper(Blueprint bp) {
		this.supers.add(bp);
	}
	
	public void addSlot(Type type, Key name) {
		if (!slots.contains(name))
			slots.add(new Pair<>(type, name));
	}
	
	public Pair<Type, Key> getSlotInfo(Key name) {
		for (Pair<Type, Key> slot : slots) {
			if (slot.getSecond() == name) {
				return slot;
			}
		}
		
		for (Blueprint parent : supers) {
			Pair<Type, Key> slot = parent.getSlotInfo(name);
			if (slot != null)
				return slot;
		}
		
		return null;
	}
	
	public void addMethod(Key name, Code code) {
		methods.put(name, code);
	}

	public Blue createBlue() {
		return new Blue(this);
	}
}
