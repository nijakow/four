package nijakow.four.server.runtime.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.lang.c.SlotVisibility;
import nijakow.four.share.util.Pair;

public class Blueprint {
	private final String filename;
	private final List<Blueprint> supers = new ArrayList<>();
	private final List<Slot> slots = new ArrayList<>();
	private final Map<Key, Method> methods = new HashMap<>();

	public Blueprint(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String toString() {
		return filename;
	}

	public Method getMethod(Key key) {
		Method m = methods.get(key);
		if (m == null) {
			for (Blueprint s : supers) {
				m = s.getMethod(key);
				if (m != null)
					break;
			}
		}
		return m;
	}

	public void addSuper(Blueprint bp) {
		this.supers.add(0, bp);
	}

	public Slot getSlot(Key name) {
		for (Slot slot : slots) {
			if (slot.getName() == name) {
				return slot;
			}
		}

		for (Blueprint parent : supers) {
			Slot slot = parent.getSlot(name);
			if (slot != null)
				return slot;
		}

		return null;
	}

	public void addSlot(SlotVisibility visibility, Type type, Key name) {
		if (!slots.contains(name))
			slots.add(new Slot(visibility, type, name));
	}

	public void addMethod(SlotVisibility visibility, Key name, Code code) {
		methods.put(name, new Method(visibility, name, code));
	}

	public Blue createBlue() {
		return new Blue(this);
	}
}
