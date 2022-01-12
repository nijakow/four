package nijakow.four.runtime;

import java.util.HashMap;
import java.util.Map;

import nijakow.four.c.parser.ParseException;
import nijakow.four.runtime.fs.FSNode;
import nijakow.four.runtime.vm.Fiber;

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

	public static Key newGensym() {
		return new Key("<GENSYM>");
	}

	static {
		get("$the_object").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				Blue blue = args[0].asFString().getBlue(fiber.getVM().getFilesystem());
				if (blue == null)
					fiber.setAccu(Instance.getNil());
				else
					fiber.setAccu(blue);
			}
		};
		get("$is_initialized").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(args[0].asBlue().isInitialized() ? new FInteger(1) : new FInteger(0));
			}
		};
		get("$get_parent").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				Blue v = self.asBlue().getParent();
				fiber.setAccu(v == null ? Instance.getNil() : v);
			}
		};
		get("$get_sibling").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				Blue v = self.asBlue().getSibling();
				fiber.setAccu(v == null ? Instance.getNil() : v);
			}
		};
		get("$get_children").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				Blue v = self.asBlue().getChildren();
				fiber.setAccu(v == null ? Instance.getNil() : v);
			}
		};
		get("$move_to").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				self.asBlue().moveTo(args[0].isNil() ? null : args[0].asBlue());
				fiber.setAccu(self);
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
		get("$on_error").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.getVM().setErrorCallback(fiber.getVM().createCallback(args[0].asFClosure()));
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
		get("$substr").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				String str = args[0].asFString().asString();
				int start = args[1].asInt();
				int end = args[2].asInt();
				fiber.setAccu(new FString(str.substring(start, end)));
			}
		};
		get("$length").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(new FInteger(args[0].length()));
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
		get("$filetext").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				String path = args[0].asFString().asString();
				FSNode node = fiber.getVM().getFilesystem().find(path);
				if (node == null || node.asFile() == null) {
					fiber.setAccu(Instance.getNil());
				} else {
					fiber.setAccu(new FString(node.asFile().getContents()));
				}
			}
		};
		get("$filetext_set").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				String path = args[0].asFString().asString();
				String value = args[1].asFString().asString();
				FSNode node = fiber.getVM().getFilesystem().writeFile(path, value);
				if (node == null || node.asFile() == null) {
					fiber.setAccu(Instance.getNil());
					fiber.setAccu(new FInteger(0));
				} else {
					fiber.setAccu(new FInteger(1));
				}
			}
		};
		get("$filetext").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				String path = args[0].asFString().asString();
				FSNode node = fiber.getVM().getFilesystem().find(path);
				if (node == null || node.asFile() == null) {
					fiber.setAccu(Instance.getNil());
				} else {
					fiber.setAccu(new FString(node.asFile().getContents()));
				}
			}
		};
		get("$touch").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				String path = args[0].asFString().asString();
				FSNode node = fiber.getVM().getFilesystem().touchf(path);
				if (node == null || node.asFile() == null) {
					fiber.setAccu(new FInteger(0));
				} else {
					fiber.setAccu(new FInteger(1));
				}
			}
		};
		get("$recompile").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				String path = args[0].asFString().asString();
				FSNode node = fiber.getVM().getFilesystem().find(path);
				try {
					node.asFile().recompile();
					fiber.setAccu(new FInteger(1));
				} catch (ParseException | NullPointerException e) {
					// TODO: Handle this gracefully
					e.printStackTrace();
					fiber.setAccu(new FInteger(0));
				}
			}
		};
	}
}
