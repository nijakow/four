package nijakow.four.c.runtime;

import java.util.HashMap;
import java.util.Map;

import nijakow.four.c.runtime.vm.Fiber;
import nijakow.four.c.runtime.vm.VM;
import nijakow.four.util.Pair;

public class Blue extends Instance {
	private static long ID_COUNTER = 0;
	private final long id;
	private boolean initialized = false;
	private Blueprint blueprint;
	private Map<Key, Instance> slots = new HashMap<>();
	private Blue parent, sibling, children;
	
	private Blue() {
		this.id = ID_COUNTER++;
		parent = sibling = children = null;
	}
	
	public Blue(Blueprint blueprint) {
		this();
		updateBlueprint(blueprint);
	}
	
	private Blue(Blue other) {
		this();
		this.blueprint = other.blueprint;
		for (Key k : other.slots.keySet())
			slots.put(k, other.slots.get(k));
	}

	public Blue getParent() { return parent; }
	public Blue getSibling() { return sibling; }
	public Blue getChildren() { return children; }


	private void removeChild(Blue child) {
		if (children == child) children = children.getSibling();
		else {
			Blue c = getChildren();
			while (c != null) {
				if (c.getSibling() == child) {
					c.sibling = c.getSibling().getSibling();
				} else {
					c = c.getSibling();
				}
			}
		}
	}

	protected void unlink() {
		if (parent == null) return;
		parent.removeChild(this);
		parent = null;
		sibling = null;
	}

	public void moveTo(Blue blue) {
		unlink();
		if (blue != null) {
			parent = blue;
			sibling = blue.getChildren();
			blue.children = this;
		}
	}

	public boolean isInitialized() { return initialized; }
	public void setInitialized() { initialized = true; }
	
	@Override
	public String toString() {
		return blueprint.toString() + "#" + id;
	}
	
	@Override
	public boolean asBoolean() {
		return true;
	}
	
	@Override
	public Blue asBlue() {
		return this;
	}

	@Override
	public void loadSlot(Fiber fiber, Key key) {
		Pair<Type, Key> info = blueprint.getSlotInfo(key);
		if (info == null)
			super.loadSlot(fiber, key);
		else if (slots.containsKey(key))
			fiber.setAccu(slots.get(key));
		else
			fiber.setAccu(Instance.getNil());
	}
	
	@Override
	public void storeSlot(Fiber fiber, Key key, Instance value) {
		Pair<Type, Key> info = blueprint.getSlotInfo(key);
		if (info == null || !info.getFirst().check(value))
			throw new RuntimeException("Oof. Slot not found (or wrong type): " + key);
		slots.put(key, value);
	}
	
	@Override
	public void send(Fiber fiber, Key key, int args) {
		Code code = blueprint.getMethod(key);
		if (code == null)
			super.send(fiber, key, args);
		else
			code.invoke(fiber, args, this);
	}
	
	@Override
	public Code extractMethod(VM vm, Key key) {
		return blueprint.getMethod(key);
	}
	
	public Blue clone() {
		return new Blue(this);
	}

    public void updateBlueprint(Blueprint blueprint) {
		this.initialized = false;
		this.blueprint = blueprint;
    }
}
