package nijakow.four.c.runtime;

import java.util.HashMap;
import java.util.Map;

import nijakow.four.c.runtime.vm.Fiber;

public class Key {
	private final String name;
	private Code code;
	
	private Key(String name) {
		this.name = name;
		this.code = null;
	}

	@Override
	public String toString() {
		return "'" + this.name;
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
		get("$the_object").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(args[0].asFString().getBlue(fiber.getVM().getFilesystem()));
			}
		};
		get("$is_initialized").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(args[0].asBlue().isInitialized() ? new FInteger(1) : new FInteger(0));
			}
		};
		get("$set_initialized").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				args[0].asBlue().setInitialized();
			}
		};
		get("$clone_instance").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(args[0].asBlue().clone());
			}
		};
		get("$call").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				for (int x = 1; x < args.length; x++)
					fiber.push(args[x]);
				args[0].asFClosure().invoke(fiber, args.length - 1);
			}
		};
		get("$log").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				for (int x = 0; x < args.length; x++)
					System.out.print(args[x].asString());
			}
		};
		get("$pause").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.getVM().invokeIn(args[0].asBlue(), args[1].asKey(), args[2].asInt());
			}
		};
		get("$on_connect").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.getVM().setConnectCallback(fiber.getVM().createCallback(args[0].asFClosure()));
			}
		};
		get("$on_receive").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				args[0].asFConnection().onReceive(fiber.getVM().createCallback(args[1].asFClosure()));
			}
		};
		get("$on_disconnect").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				args[0].asFConnection().onDisconnect(fiber.getVM().createCallback(args[1].asFClosure()));
			}
		};
		get("$write").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				for (int x = 1; x < args.length; x++)
					args[0].asFConnection().send(args[x]);
			}
		};
		get("$close").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				args[0].asFConnection().close();
			}
		};
		get("$chr").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(new FString("" + (char) args[0].asInt()));
			}
		};
		get("$strlen").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(new FInteger(args[0].asFString().asString().length()));
			}
		};
		get("$substr").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				String str = args[0].asFString().asString();
				int start = args[1].asInt();
				int end = args[2].asInt();
				fiber.setAccu(new FString(str.substring(start, end)));
			}
		};
		get("$listlen").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(new FInteger(args[0].asFList().getSize()));
			}
		};
		get("$listinsert").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				args[0].asFList().insert(args[1].asInt(), args[2]);
			}
		};
		get("$listremove").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(args[0].asFList().remove(args[1].asInt()));
			}
		};
		get("$random").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(new FInteger((int) (Math.random() * Integer.MAX_VALUE)));
			}
		};
	}
}
