package nijakow.four.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.parser.ParseException;
import nijakow.four.runtime.fs.FSNode;
import nijakow.four.runtime.fs.ImmutableException;
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

	private static final Map<String, Key> KEYS = new HashMap<>();
	public static Key get(String name) {
		if (!KEYS.containsKey(name))
			KEYS.put(name, new Key(name));
		return KEYS.get(name);
	}

	public static Key newGensym() {
		return new Key("<GENSYM>");
	}

	static {
		get("$statics").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException, CompilationException, ParseException {
				fiber.setAccu(fiber.getSharedState().getStatics());
			}
		};
		get("$the_object").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException, CompilationException, ParseException {
				Blue blue = args[0].asFString().getBlueWithErrors(fiber.getVM().getFilesystem());
				if (blue == null)
					fiber.setAccu(Instance.getNil());
				else
					fiber.setAccu(blue);
			}
		};
		get("$is_initialized").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				fiber.setAccu(args[0].asBlue().isInitialized() ? new FInteger(1) : new FInteger(0));
			}
		};
		get("$get_parent").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				Blue v = self.asBlue().getParent();
				fiber.setAccu(v == null ? Instance.getNil() : v);
			}
		};
		get("$get_sibling").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				Blue v = self.asBlue().getSibling();
				fiber.setAccu(v == null ? Instance.getNil() : v);
			}
		};
		get("$get_children").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				Blue v = self.asBlue().getChildren();
				fiber.setAccu(v == null ? Instance.getNil() : v);
			}
		};
		get("$move_to").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				self.asBlue().moveTo(args[0].isNil() ? null : args[0].asBlue());
				fiber.setAccu(self);
			}
		};
		get("$set_initialized").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				args[0].asBlue().setInitialized();
			}
		};
		get("$clone_instance").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				fiber.setAccu(args[0].asBlue().cloneBlue());
			}
		};
		get("$call").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				for (int x = 1; x < args.length; x++)
					fiber.push(args[x]);
				args[0].asFClosure().invoke(fiber, args.length - 1);
			}
		};
		get("$log").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) {
				for (Instance arg : args) System.out.print(arg.asString());
			}
		};
		get("$pause").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				fiber.getVM().invokeIn(fiber.getSharedState(), args[0].asBlue(), args[1].asKey(), args[2].asInt());
			}
		};
		get("$on_connect").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				fiber.getVM().setConnectCallback(fiber.getVM().createCallback(null, args[0].asFClosure()));
			}
		};
		get("$on_receive").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				args[0].asFConnection().onReceive(fiber.getVM().createCallback(fiber.getSharedState(), args[1].asFClosure()));
			}
		};
		get("$on_disconnect").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				args[0].asFConnection().onDisconnect(fiber.getVM().createCallback(fiber.getSharedState(), args[1].asFClosure()));
			}
		};
		get("$on_error").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				fiber.getVM().setErrorCallback(fiber.getVM().createCallback(fiber.getSharedState(), args[0].asFClosure()));
			}
		};
		get("$write").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				for (int x = 1; x < args.length; x++)
					args[0].asFConnection().send(args[x]);
			}
		};
		get("$close").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
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
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
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
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				args[0].asFList().insert(args[1].asInt(), args[2]);
			}
		};
		get("$listremove").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
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
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
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
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				String path = args[0].asFString().asString();
				String value = args[1].asFString().asString();
				FSNode node;
				try {
					node = fiber.getVM().getFilesystem().writeFile(path, value);
				} catch (ImmutableException e) {
					node = null;
				}
				if (node == null || node.asFile() == null) {
					fiber.setAccu(Instance.getNil());
					fiber.setAccu(new FInteger(0));
				} else {
					fiber.setAccu(new FInteger(1));
				}
			}
		};
		get("$filechildren").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				String path = args[0].asFString().asString();
				List<FSNode> contents = fiber.getVM().getFilesystem().listChildren(path);
				if (contents.isEmpty()) {
					fiber.setAccu(Instance.getNil());
				} else {
					FList lst = new FList();
					for (FSNode node : contents) {
						lst.insert(-1, new FString(node.getName()));
					}
					fiber.setAccu(lst);
				}
			}
		};
		get("$touch").code = new BuiltinCode() {

			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				String path = args[0].asFString().asString();
				FSNode node;
				try {
					node = fiber.getVM().getFilesystem().touchf(path);
				} catch (ImmutableException e) {
					node = null;
				}
				if (node == null || node.asFile() == null) {
					fiber.setAccu(new FInteger(0));
				} else {
					fiber.setAccu(new FInteger(1));
				}
			}
		};
		get("$recompile").code = new BuiltinCode() {
			
			@Override
			void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				String path = args[0].asFString().asString();
				FSNode node = fiber.getVM().getFilesystem().find(path);
				try {
					if (node == null || node.asFile() == null)
						fiber.setAccu(new FInteger(0));
					else {
						node.asFile().recompile();
						fiber.setAccu(new FInteger(1));
					}
				} catch (ParseException | NullPointerException | CompilationException e) {
					// TODO: Handle this gracefully
					e.printStackTrace();
					fiber.setAccu(new FInteger(0));
				}
			}
		};
	}
}
