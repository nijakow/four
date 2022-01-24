package nijakow.four.runtime.objects;

import java.util.HashMap;
import java.util.Map;

import nijakow.four.runtime.vm.code.Code;
import nijakow.four.runtime.Key;
import nijakow.four.runtime.types.Type;
import nijakow.four.runtime.exceptions.FourRuntimeException;
import nijakow.four.runtime.vm.Fiber;
import nijakow.four.runtime.vm.VM;
import nijakow.four.serialization.base.ISerializer;
import nijakow.four.util.Pair;

public class Blue extends Instance {
	private static long ID_COUNTER = 0;
	private final long id;
	private boolean initialized = false;
	private Blueprint blueprint;
	private final Map<Key, Instance> slots = new HashMap<>();
	private Blue parent, sibling, children;
	
	public Blue() {
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
		return ((blueprint == null) ? "<no blueprint>" : blueprint.toString()) + "#" + id;
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
	public void loadSlot(Fiber fiber, Key key) throws FourRuntimeException {
		Pair<Type, Key> info = blueprint.getSlotInfo(key);
		if (info == null)
			fiber.setAccu(new FClosure(this, this, key));
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
	public void send(Fiber fiber, Key key, int args) throws FourRuntimeException {
		Code code = blueprint == null ? null : blueprint.getMethod(key);
		if (code == null)
			super.send(fiber, key, args);
		else
			code.invoke(fiber, args, this);
	}
	
	@Override
	public Code extractMethod(VM vm, Key key) {
		return blueprint.getMethod(key);
	}

	public Blue cloneBlue() {
		return new Blue(this);
	}

    public void updateBlueprint(Blueprint blueprint) {
		this.initialized = false;
		this.blueprint = blueprint;
    }

	@Override
	public String getSerializationClassID() {
		return "blue";
	}

	@Override
	public void serialize(ISerializer serializer) {
		ISerializer mapser = serializer.openProperty("blue.slots");
		for (Key key : slots.keySet()) {
			mapser.openProperty(key.getName()).writeObject(slots.get(key)).close();
		}
		mapser.close();
	}
}
