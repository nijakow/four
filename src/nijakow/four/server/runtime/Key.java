package nijakow.four.server.runtime;

import nijakow.four.server.logging.CompilationLogger;
import nijakow.four.server.logging.LogLevel;
import nijakow.four.server.logging.Logger;
import nijakow.four.server.nvfs.files.Directory;
import nijakow.four.server.nvfs.files.File;
import nijakow.four.server.nvfs.files.TextFile;
import nijakow.four.server.process.filedescriptor.IFileDescriptor;
import nijakow.four.server.runtime.exceptions.CastException;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.runtime.objects.collections.FList;
import nijakow.four.server.runtime.objects.standard.FClosure;
import nijakow.four.server.runtime.objects.standard.FInteger;
import nijakow.four.server.runtime.objects.standard.FString;
import nijakow.four.server.runtime.vm.code.BuiltinCode;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.server.runtime.vm.fiber.Fiber;
import nijakow.four.server.storage.serialization.fs.BasicFSSerializer;
import nijakow.four.server.storage.serialization.fs.deserializer.BasicFSDeserializer;
import nijakow.four.server.users.Group;
import nijakow.four.server.users.Identity;
import nijakow.four.server.users.User;
import nijakow.four.share.lang.FourCompilerException;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.share.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Key {
	private final String name;
	private Code code;
	private Blueprint blueprint;
	
	private Key(String name) {
		this.name = name;
		this.code = null;
		this.blueprint = null;
	}

    @Override
	public String toString() {
		return "'" + this.name;
	}

	public String getName() { return name; }
	public Code getCode() {
		return code;
	}
	public Blueprint getBlueprint() { return blueprint; }

	public void setBlueprint(Blueprint blueprint) {
		this.blueprint = blueprint;
	}

	public Instance newBlueInstance() {
		if (this.blueprint == null)
			return Instance.getNil();
		return this.blueprint.createBlue();
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
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException, CompilationException, ParseException {
				fiber.setAccu(fiber.getSharedState().getStatics());
			}
		};
		get("$type").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException, CompilationException, ParseException {
				fiber.setAccu(new FString(args[0].getType()));
			}
		};
		get("$the_object").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException, CompilationException, ParseException {
				Blue blue = fiber.getVM().getFilesystem().getBlue(fiber.getVM(), args[0].asFString().asString());
				if (blue == null)
					fiber.setAccu(Instance.getNil());
				else
					fiber.setAccu(blue);
			}
		};
		get("$is_initialized").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				fiber.setAccu(FInteger.getBoolean(args[0].asBlue().isInitialized()));
			}
		};
		get("$get_parent").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				Blue v = self.asBlue().getParent();
				fiber.setAccu(v == null ? Instance.getNil() : v);
			}
		};
		get("$get_sibling").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				Blue v = self.asBlue().getSibling();
				fiber.setAccu(v == null ? Instance.getNil() : v);
			}
		};
		get("$get_children").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				Blue v = self.asBlue().getChildren();
				fiber.setAccu(v == null ? Instance.getNil() : v);
			}
		};
		get("$move_to").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				self.asBlue().moveTo(args[0].isNil() ? null : args[0].asBlue());
				fiber.setAccu(self);
			}
		};
		get("$set_initialized").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				args[0].asBlue().setInitialized();
			}
		};
		get("$clone_instance").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				fiber.setAccu(args[0].asBlue().cloneBlue());
			}
		};
		get("$call").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				for (int x = 1; x < args.length; x++)
					fiber.push(args[x]);
				args[0].asFClosure().invoke(fiber, args.length - 1);
			}
		};
		get("$sleep").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				args[0].asFClosure().invokeIn(fiber, args[1].asInt(), args.length - 1);
			}
		};
		get("$log").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) {
				final Logger logger = fiber.getVM().getLogger();
				for (Instance arg : args) {
					logger.print(LogLevel.INTERNAL, arg.asString());
				}
			}
		};
		get("$pause").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				fiber.getVM().invokeIn(fiber.getSharedState(), args[0].asBlue(), args[1].asKey(), args[2].asInt());
			}
		};
		get("$on_connect").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				fiber.getVM().setConnectCallback(fiber.getVM().createCallback(null, args[0].asFClosure()));
			}
		};
		get("$on_receive").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				args[0].asFConnection().onReceive(fiber.getVM().createCallback(fiber.getSharedState(), args[1].asFClosure()));
			}
		};
		get("$on_escape").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				args[0].asFConnection().onEscape(fiber.getVM().createCallback(fiber.getSharedState(), args[1].asFClosure()));
			}
		};
		get("$on_disconnect").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				args[0].asFConnection().onDisconnect(fiber.getVM().createCallback(fiber.getSharedState(), args[1].asFClosure()));
			}
		};
		get("$on_error").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				fiber.getVM().setErrorCallback(fiber.getVM().createCallback(fiber.getSharedState(), args[0].asFClosure()));
			}
		};
		get("$write").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				for (int x = 1; x < args.length; x++)
					args[0].asFConnection().send(args[x]);
			}
		};
		get("$close").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				args[0].asFConnection().close();
			}
		};
		get("$chr").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(new FString("" + (char) args[0].asInt()));
			}
		};
		get("$substr").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				String str = args[0].asFString().asString();
				int start = args[1].asInt();
				int end = args[2].asInt();
				fiber.setAccu(new FString(str.substring(start, end)));
			}
		};
		get("$length").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(FInteger.get(args[0].length()));
			}
		};
		get("$listinsert").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				args[0].asFList().insert(args[1].asInt(), args[2]);
			}
		};
		get("$listremove").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				fiber.setAccu(args[0].asFList().remove(args[1].asInt()));
			}
		};
		get("$random").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) {
				fiber.setAccu(FInteger.get((int) (Math.random() * Integer.MAX_VALUE)));
			}
		};
		get("$filetext").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				String path = args[0].asFString().asString();
				TextFile node = fiber.getVM().getFilesystem().resolveTextFile(path);
				if (node == null) {
					fiber.setAccu(Instance.getNil());
				} else {
					String contents = node.readContents(fiber.getSharedState().getUser());
					if (contents != null)
						fiber.setAccu(new FString(node.getContents()));
					else
						fiber.setAccu(Instance.getNil());
				}
			}
		};
		get("$filetext_set").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				String path = args[0].asFString().asString();
				String value = args[1].asFString().asString();
				File file = fiber.getVM().getFilesystem().resolve(path);
				if (file != null && file.asTextFile() != null
						&& file.asTextFile().writeContents(value, fiber.getSharedState().getUser())) {
					fiber.setAccu(FInteger.getBoolean(true));
				} else {
					fiber.setAccu(FInteger.getBoolean(false));
				}
			}
		};
		get("$filechildren").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				String path = args[0].asFString().asString();
				Directory dir = fiber.getVM().getFilesystem().resolveDirectory(path);
				Pair<String, File>[] contents = null;
				if (dir != null && dir.getRights().checkReadAccess(fiber.getSharedState().getUser()))
					contents = dir.ls();
				if (contents == null) {
					fiber.setAccu(Instance.getNil());
				} else {
					FList lst = new FList();
					for (Pair<String, File> node : contents) {
						lst.insert(-1, new FString(node.getFirst()));
					}
					fiber.setAccu(lst);
				}
			}
		};
		get("$touch").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				String path = args[0].asFString().asString();
				TextFile file = fiber.getVM().getFilesystem().touch(path,
						fiber.getSharedState().getUser(),
						fiber.getSharedState().getUser(),
						fiber.getVM().getIdentityDB().getUsersGroup());
				fiber.setAccu(FInteger.getBoolean(file != null));
			}
		};
		get("$mkdir").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				String path = args[0].asFString().asString();
				if (fiber.getVM().getFilesystem().mkdir(path,
						fiber.getSharedState().getUser(),
						fiber.getSharedState().getUser(),
						fiber.getVM().getIdentityDB().getUsersGroup()) != null) {
					fiber.setAccu(FInteger.getBoolean(true));
				} else {
					fiber.setAccu(FInteger.getBoolean(false));
				}
			}
		};
		get("$rm").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				String curPath = args[0].asFString().asString();
				File file = fiber.getVM().getFilesystem().resolve(curPath);
				if (file != null) {
					fiber.setAccu(FInteger.getBoolean(file.rm(fiber.getSharedState().getUser())));
				} else {
					fiber.setAccu(FInteger.getBoolean(false));
				}
			}
		};
		get("$mv").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				if (fiber.getVM().getFilesystem().mv(args[0].asFString().asString(),
						args[1].asFString().asString(),
						fiber.getSharedState().getUser()))
					fiber.setAccu(FInteger.getBoolean(true));
				else
					fiber.setAccu(FInteger.getBoolean(false));
			}
		};
		get("$recompile").code = new BuiltinCode() {
			
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				String path = args[0].asFString().asString();
				CompilationLogger logger = fiber.getVM().getLogger().newCompilationLogger();
				try {
					TextFile file = fiber.getVM().getFilesystem().resolveTextFile(path);
					if (file == null || !file.getRights().checkReadAccess(fiber.getSharedState().getUser()))
						fiber.setAccu(new FString(""));
					else {
						file.compile(logger);
						fiber.setAccu(Instance.getNil());
					}
				} catch (FourCompilerException e) {
					logger.tell(e);
					fiber.setAccu(new FString(logger.getTranscript()));
				}
			}
		};
		get("$eval").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				Code code = args[1].asFString().compileAsCode();
				if (code != null) {
					for (int x = 2; x < args.length; x++)
						fiber.push(args[x]);
					code.invoke(fiber, args.length - 2, args[0]);
				}
			}
		};
		get("$getuid").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				fiber.setAccu(new FString(fiber.getSharedState().getUser().getName()));
			}
		};
		get("$uname").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String id = args[0].asFString().asString();
				Identity user = fiber.getVM().getIdentityDB().getIdentityByName(id);
				if (user != null && user.asUser() != null)
					fiber.setAccu(new FString(user.getName()));
				else
					fiber.setAccu(Instance.getNil());
			}
		};
		get("$gname").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String id = args[0].asFString().asString();
				Identity user = fiber.getVM().getIdentityDB().getIdentityByName(id);
				if (user != null && user.asGroup() != null)
					fiber.setAccu(new FString(user.getName()));
				else
					fiber.setAccu(Instance.getNil());
			}
		};
		get("$checkexec").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String curPath = args[0].asFString().asString();
				File file = fiber.getVM().getFilesystem().resolve(curPath);
				fiber.setAccu(FInteger.getBoolean((file != null && file.getRights().checkExecuteAccess(fiber.getSharedState().getUser()))));
			}
		};
		get("$stat").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String curPath = args[0].asFString().asString();
				File file = fiber.getVM().getFilesystem().resolve(curPath);
				fiber.setAccu(FInteger.get((file != null) ? file.getmod() : -1));
			}
		};
		get("$getown").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String curPath = args[0].asFString().asString();
				File file = fiber.getVM().getFilesystem().resolve(curPath);
				fiber.setAccu((file != null) ? new FString(file.getRights().getUserAccessRights().getIdentity().getName()) : Instance.getNil());
			}
		};
		get("$getgrp").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String curPath = args[0].asFString().asString();
				File file = fiber.getVM().getFilesystem().resolve(curPath);
				fiber.setAccu((file != null) ? new FString(file.getRights().getGroupAccessRights().getIdentity().getName()) : Instance.getNil());
			}
		};
		get("$chmod").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String curPath = args[0].asFString().asString();
				final int flags = args[1].asInt();
				File file = fiber.getVM().getFilesystem().resolve(curPath);
				fiber.setAccu(FInteger.getBoolean((file != null && file.chmod(fiber.getSharedState().getUser(), flags))));
			}
		};
		get("$chown").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String curPath = args[0].asFString().asString();
				final Identity identity = fiber.getVM().getIdentityDB().getIdentityByName(args[1].asFString().asString());
				if (identity == null)
					fiber.setAccu(FInteger.getBoolean(false));
				else {
					File file = fiber.getVM().getFilesystem().resolve(curPath);
					fiber.setAccu(FInteger.getBoolean((file != null && file.chown(fiber.getSharedState().getUser(), identity))));
				}
			}
		};
		get("$chgrp").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String curPath = args[0].asFString().asString();
				final Identity identity = fiber.getVM().getIdentityDB().getIdentityByName(args[1].asFString().asString());
				if (identity == null)
					fiber.setAccu(FInteger.getBoolean(false));
				else {
					File file = fiber.getVM().getFilesystem().resolve(curPath);
					fiber.setAccu(FInteger.getBoolean((file != null && file.chgrp(fiber.getSharedState().getUser(), identity))));
				}
			}
		};
		get("$login").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String username = args[0].asFString().asString();
				final String password = args[1].asFString().asString();
				User user = fiber.getVM().getIdentityDB().login(username, password);
				if (user != null) {
					fiber.getVM().getLogger().println(LogLevel.INFO, "Login user '" + user.getName() + "'");
					fiber.getSharedState().setUser(user);
					fiber.setAccu(FInteger.getBoolean(true));
				} else {
					fiber.setAccu(FInteger.getBoolean(false));
				}
			}
		};
		get("$finduser").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String username = args[0].asFString().asString();
				Identity identity = fiber.getVM().getIdentityDB().getIdentityByName(username);
				if (identity != null && identity.asUser() != null)
					fiber.setAccu(new FString(identity.getName()));
				else
					fiber.setAccu(Instance.getNil());
			}
		};
		get("$findgroup").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String username = args[0].asFString().asString();
				Identity identity = fiber.getVM().getIdentityDB().getIdentityByName(username);
				if (identity != null && identity.asGroup() != null)
					fiber.setAccu(new FString(identity.getName()));
				else
					fiber.setAccu(Instance.getNil());
			}
		};
		get("$members").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final FList list = new FList();
				final Group group = fiber.getVM().getIdentityDB().getGroupByName(args[0].asFString().asString());
				if (group != null) {
					for (Identity identity : group.getMembers()) {
						list.insert(list.length(), new FString(identity.getName()));
					}
				}
				fiber.setAccu(list);
			}
		};
		get("$groups").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				FList list = new FList();
				for (Identity identity : fiber.getVM().getIdentityDB().getIdentities()) {
					Group group = identity.asGroup();
					if (group != null)
						list.insert(list.length(), new FString(group.getName()));
				}
				fiber.setAccu(list);
			}
		};
		get("$adduser").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String username = args[0].asFString().asString();
				User user = null;
				if (fiber.isRoot()) {
					user = fiber.getVM().getIdentityDB().newUser(username);
				}
				fiber.setAccu((user != null) ? new FString(user.getName()) : Instance.getNil());
			}
		};
		get("$deluser").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String username = args[0].asFString().asString();
				boolean result = fiber.isRoot();
				if (result)
					result = fiber.getVM().getIdentityDB().deleteIdentity(username);
				fiber.setAccu(FInteger.getBoolean(result));
			}
		};
		get("$addgroup").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String username = args[0].asFString().asString();
				Group group = null;
				if (fiber.isRoot()) {
					group = fiber.getVM().getIdentityDB().newGroup(username);
				}
				fiber.setAccu((group != null) ? new FString(group.getName()) : Instance.getNil());
			}
		};
		get("$delgroup").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final String username = args[0].asFString().asString();
				boolean result = fiber.isRoot();
				if (result)
					result = fiber.getVM().getIdentityDB().deleteIdentity(username);
				fiber.setAccu(FInteger.getBoolean(result));
			}
		};
		get("$addtogroup").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final Identity ident = fiber.getVM().getIdentityDB().getIdentityByName(args[0].asFString().asString());
				final Group group = fiber.getVM().getIdentityDB().getGroupByName(args[1].asFString().asString());
				boolean result = fiber.isRoot() && ident != null && group != null;
				if (result)
					group.add(ident);
				fiber.setAccu(FInteger.getBoolean(result));
			}
		};
		get("$removefromgroup").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final Identity ident = fiber.getVM().getIdentityDB().getIdentityByName(args[0].asFString().asString());
				final Group group = fiber.getVM().getIdentityDB().getGroupByName(args[1].asFString().asString());
				boolean result = fiber.isRoot() && ident != null && group != null;
				if (result)
					group.remove(ident);
				fiber.setAccu(FInteger.getBoolean(result));
			}
		};
		get("$isactive").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				final User user = fiber.getVM().getIdentityDB().getUserByName(args[0].asFString().asString());
				fiber.setAccu(FInteger.getBoolean(user != null && user.isCurrentlyActive()));
			}
		};
		get("$chpass").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				boolean success = false;
				final String uid = args[0].asFString().asString();
				final String password = args[1].asFString().asString();
				User user = fiber.getVM().getIdentityDB().getUserByName(uid);
				if (user != null && (fiber.getSharedState().getUser() == user || fiber.isRoot())) {
					user.setPassword(password);
					success = true;
				}
				fiber.setAccu(FInteger.getBoolean(success));
			}
		};
		get("$getshell").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				Instance value = Instance.getNil();
				final String username = args[0].asFString().asString();
				User user = fiber.getVM().getIdentityDB().getUserByName(username);
				if (user != null && (fiber.getSharedState().getUser() == user || fiber.isRoot())) {
					String shell = user.getShell();
					if (shell != null)
						value = new FString(shell);
				}
				fiber.setAccu(value);
			}
		};
		get("$chsh").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws FourRuntimeException {
				boolean success = false;
				final String username = args[0].asFString().asString();
				final String shell = args[1].asFString().asString();
				User user = fiber.getVM().getIdentityDB().getUserByName(username);
				if (user != null && (fiber.getSharedState().getUser() == user || fiber.isRoot())) {
					user.setShell(shell);
					success = true;
				}
				fiber.setAccu(FInteger.getBoolean(success));
			}
		};
		get("$dumpfs").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				fiber.setAccu(FInteger.getBoolean(fiber.getVM().getFour().takeSnapshot()));
			}
		};
		get("$loadfs").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				fiber.setAccu(FInteger.getBoolean(fiber.getVM().getFour().loadLatestSnapshot()));
			}
		};
		get("$compress").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final String path = args[0].asFString().asString();
				final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				BasicFSSerializer serializer = new BasicFSSerializer(outputStream);
				final File file = fiber.getVM().getFilesystem().resolve(path);
				// TODO: Check if file != null
				if (file == null) {
					fiber.setAccu(Instance.getNil());
				} else {
					serializer.serialize(file);
					try {
						fiber.setAccu(new FString(new String(outputStream.toByteArray(), StandardCharsets.UTF_8)));
						outputStream.close();
					} catch (IOException e) {
						fiber.setAccu(Instance.getNil());
					}
				}
			}
		};
		get("$uncompress").code = new BuiltinCode() {
			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final String path = args[0].asFString().asString();
				final String dirpath = args[1].asFString().asString();
				final TextFile file = fiber.getVM().getFilesystem().resolveTextFile(path);
				final Directory dir = fiber.getVM().getFilesystem().resolveDirectory(dirpath);
				if (file == null || dir == null) {
					fiber.setAccu(FInteger.getBoolean(false));
				} else {
					final String contents = file.getContents();
					final ByteArrayInputStream inputStream = new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));
					BasicFSDeserializer deserializer = new BasicFSDeserializer(inputStream);
					deserializer.restore(fiber.getVM().getFilesystem(), fiber.getVM().getIdentityDB(), dir);
					fiber.setAccu(FInteger.getBoolean(true));
				}
			}
		};
		get("$getmsgs").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final int amount = args[0].asInt();
				FList lst = new FList();
				for (String s : fiber.getVM().getLogger().getLines(amount))
					lst.append(new FString(s));
				fiber.setAccu(lst);
			}
		};
		get("$httpdl").code = new BuiltinCode() {

			private byte[] slurp(InputStream input) throws IOException {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();

				int nRead;
				byte[] data = new byte[16384];

				while ((nRead = input.read(data, 0, data.length)) != -1) {
					buffer.write(data, 0, nRead);
				}

				return buffer.toByteArray();
			}

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				try {
					final URL url = new URL(args[0].asFString().asString());
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					InputStream responseStream = connection.getInputStream();
					byte[] result = slurp(responseStream);
					fiber.setAccu(new FString(new String(result, StandardCharsets.UTF_8)));
				} catch (MalformedURLException e) {
					fiber.setAccu(Instance.getNil());
				} catch (IOException e) {
					fiber.getVM().getLogger().printException(e);
					fiber.setAccu(Instance.getNil());
				}
			}
		};
		get("$blueprints").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				FList lst = new FList();
				for (Blueprint bp : Blueprint.getAllBlueprints()) {
					lst.append(new FString(bp.getFilename()));
				}
				fiber.setAccu(lst);
			}
		};
		get("$interface").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final Blueprint blueprint = Blueprint.findBlueprint(args[0].asFString().asString());
				final boolean privatesAlso = false;
				FList lst = new FList();
				if (blueprint != null) {
					for (String line : blueprint.getInterface(privatesAlso)) {
						lst.append(new FString(line));
					}
				}
				fiber.setAccu(lst);
			}
		};
		get("$definitions").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final Key key = Key.get(args[0].asFString().asString());
				FList lst = new FList();
				for (Blueprint bp : Blueprint.findBlueprintsImplementingKey(key)) {
					lst.append(new FString(bp.getFilename()));
				}
				fiber.setAccu(lst);
			}
		};
		get("$implementors").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final Blueprint blueprint = Blueprint.findBlueprint(args[0].asFString().asString());
				FList lst = new FList();
				for (Blueprint bp : Blueprint.findBlueprintsExtending(blueprint)) {
					lst.append(new FString(bp.getFilename()));
				}
				fiber.setAccu(lst);
			}
		};
		get("$supers").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final Blueprint blueprint = Blueprint.findBlueprint(args[0].asFString().asString());
				FList lst = new FList();
				if (blueprint != null) {
					for (Blueprint bp : blueprint.getSupers()) {
						lst.append(new FString(bp.getFilename()));
					}
				}
				fiber.setAccu(lst);
			}
		};
		get("$syminfo").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final Blueprint blueprint = Blueprint.findBlueprint(args[0].asFString().asString());
				final Key sym = Key.get(args[1].asFString().asString());
				FList lst = new FList();
				String result = null;
				if (blueprint != null)
					result = blueprint.getSymInfo(sym);
				fiber.setAccu(result == null ? Instance.getNil() : new FString(result));
			}
		};
		get("$syscall_open").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final String path = args[0].asFString().asString();
				final int flags = args[1].asInt();
				int fd = fiber.getSharedState().open(path, (flags & 0x01) != 0, (flags & 0x02) != 0);
				fiber.setAccu(FInteger.get(fd));
			}
		};
		get("$syscall_read").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final int fd = args[0].asInt();
				final FList buffer = args[1].asFList();
				IFileDescriptor descriptor = fiber.getSharedState().get(fd);
				if (descriptor != null) {
					fiber.setAccu(FInteger.get(descriptor.read(buffer)));
				} else {
					fiber.setAccu(FInteger.get(-1));
				}
			}
		};
		get("$syscall_close").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final int fd = args[0].asInt();
				fiber.setAccu(FInteger.getBoolean(fiber.getSharedState().close(fd)));
			}
		};
		get("$base64_encode").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final String text = args[0].asFString().asString();
				fiber.setAccu(new FString(Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8))));
			}
		};
		get("$base64_decode").code = new BuiltinCode() {

			@Override
			public void run(Fiber fiber, Instance self, Instance[] args) throws CastException {
				final String text = args[0].asFString().asString();
				try {
					fiber.setAccu(new FString(new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8)));
				} catch (IllegalArgumentException e) {
					fiber.setAccu(Instance.getNil());
				}
			}
		};
	}
}
