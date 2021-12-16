package nijakow.four.c.runtime;

import java.util.HashMap;
import java.util.Map;

public class Key {
	private final String name;
	private final Code code;
	
	private Key(String name) {
		this.name = name;
		this.code = null;
	}

	public Code getCode() {
		return code;
	}
	
	
	private static Map<String, Key> KEYS = new HashMap<>();
	public static Key get(String name) {
		if (!KEYS.containsKey(name))
			KEYS.put(name, new Key(name));
		return KEYS.get(name);
	}
}
