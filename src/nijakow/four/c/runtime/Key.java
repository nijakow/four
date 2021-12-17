package nijakow.four.c.runtime;

import java.util.HashMap;
import java.util.Map;

import nijakow.four.c.runtime.vm.Callback;
import nijakow.four.c.runtime.vm.Fiber;

public class Key {
	private final String name;
	private Code code;
	
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
	
	static {
		get("say_hi").code = (fiber, args, self) -> System.out.println("This is the hi function!");
		get("log").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Blue self, Instance[] args) {
				System.out.println(args[0].asString());
			}
		};
		get("on_connect").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Blue self, Instance[] args) {
				fiber.getVM().setConnectCallback(fiber.getVM().createCallback(args[0].asBlue(), args[1].asKey()));
			}
		};
		get("pause").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Blue self, Instance[] args) {
				fiber.getVM().invokeIn(args[0].asBlue(), args[1].asKey(), args[2].asInt());
			}
		};
		get("write").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Blue self, Instance[] args) {
				args[0].asFConnection().send(args[1]);
			}
		};
	}
}
