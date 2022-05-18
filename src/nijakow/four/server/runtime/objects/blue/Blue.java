package nijakow.four.server.runtime.objects.blue;

import java.util.HashMap;
import java.util.Map;

import nijakow.four.server.runtime.objects.FloatingInstance;
import nijakow.four.server.runtime.objects.standard.FClosure;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.vm.fiber.Fiber;
import nijakow.four.server.runtime.vm.VM;

public class Blue extends FloatingInstance {
	private static long ID_COUNTER = 0;
	private final long id;
	private boolean initialized = false;
	private Blueprint blueprint;
	private final Map<Key, Instance> slots = new HashMap<>();
	private Blue parent, sibling, children;
	
	public Blue() {
		this.id = ID_COUNTER++;
		parent = sibling = children = null;
		registerToPool();
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
		return ((blueprint == null) ? "<no blueprint>" : blueprint.toString()) + "#" + id;
	}

	@Override
	public String getType() { return "object"; }

	@Override
	public boolean asBoolean() {
		return true;
	}
	
	@Override
	public Blue asBlue() {
		return this;
	}

	@Override
	public void loadSlot(Fiber fiber, Key key) throws FourRuntimeException {
		Slot slot = blueprint.getSlot(key);
		if (slot == null)
			fiber.setAccu(new FClosure(null, this, this, key));
		else if (slots.containsKey(key))
			fiber.setAccu(slots.get(key));
		else
			fiber.setAccu(Instance.getNil());
	}
	
	@Override
	public void storeSlot(Fiber fiber, Key key, Instance value) {
		Slot slot = blueprint.getSlot(key);
		if (slot == null || !slot.getType().check(value))
			throw new RuntimeException("Oof. Slot not found (or wrong type): " + key);
		slots.put(key, value);
	}
	
	@Override
	public void send(Fiber fiber, Key key, int args) throws FourRuntimeException {
		Method method = blueprint == null ? null : blueprint.getMethod(key);
		if (method == null)
			super.send(fiber, key, args);
		else
			method.getCode().invoke(fiber, args, this);
	}
	
	@Override
	public Code extractMethod(VM vm, Key key) {
		Method method = blueprint.getMethod(key);
		return (method != null) ? method.getCode() : null;
	}

	public Blue cloneBlue() {
		return new Blue(this);
	}

	public Blueprint getBlueprint() { return this.blueprint; }

    public void updateBlueprint(Blueprint blueprint) {
		this.initialized = false;
		this.blueprint = blueprint;
    }
}
