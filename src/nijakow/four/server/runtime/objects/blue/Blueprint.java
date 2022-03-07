package nijakow.four.server.runtime.objects.blue;

import java.util.*;

import nijakow.four.server.runtime.objects.FloatingInstance;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.lang.c.SlotVisibility;

public class Blueprint {
	private static final Set<Blueprint> instances;

	static {
		instances = Collections.newSetFromMap(new WeakHashMap<>());
	}

	public static Blueprint[] getAllBlueprints() {
		return instances.toArray(new Blueprint[0]);
	}

	public static Blueprint findBlueprint(String name) {
		for (Blueprint bp : getAllBlueprints())
			if (name.equals(bp.getFilename()))
				return bp;
		return null;
	}

	public static Blueprint[] findBlueprintsImplementingKey(Key key) {
		List<Blueprint> implementors = new ArrayList<>();
		for (Blueprint bp : instances) {
			if (bp == null) continue;
			if (bp.implementsKey(key)) {
				implementors.add(bp);
			}
		}
		return implementors.toArray(new Blueprint[0]);
	}

	public static Blueprint[] findBlueprintsExtending(Blueprint blueprint) {
		List<Blueprint> implementors = new ArrayList<>();
		for (Blueprint bp : instances) {
			if (bp == null) continue;
			if (bp.extendsBlueprint(blueprint)) {
				implementors.add(bp);
			}
		}
		return implementors.toArray(new Blueprint[0]);
	}

	private final String filename;
	private final List<Blueprint> supers = new ArrayList<>();
	private final List<Slot> slots = new ArrayList<>();
	private final Map<Key, Method> methods = new HashMap<>();

	public Blueprint(String filename) {
		this.filename = filename;
		instances.add(this);
	}
	
	@Override
	public String toString() {
		return filename;
	}

	public String getFilename() {
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

	private boolean implementsKey(Key key) {
		for (Slot s : slots)
			if (s.getName() == key)
				return true;
		return methods.containsKey(key);
	}

	private boolean extendsBlueprint(Blueprint bp) {
		for (Blueprint parent : supers) {
			if (parent == bp)
				return true;
		}
		return false;
	}
}
