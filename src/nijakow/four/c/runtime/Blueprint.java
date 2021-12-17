package nijakow.four.c.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nijakow.four.c.runtime.vm.VM;

public class Blueprint {
	private List<Blueprint> supers = new ArrayList<>();
	private List<Key> slots = new ArrayList<>();
	private Map<Key, Code> methods = new HashMap<>();

	public Blueprint() {}

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
			slots.add(name);
	}
	
	public void addMethod(Key name, Code code) {
		methods.put(name, code);
	}

	public Blue createBlue() {
		return new Blue(this);
	}

}
