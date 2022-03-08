package nijakow.four.server.runtime.vm;

import nijakow.four.server.logging.LogLevel;
import nijakow.four.server.logging.Logger;
import nijakow.four.server.net.Server;
import nijakow.four.server.runtime.*;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.nvfs.NVFileSystem;
import nijakow.four.server.runtime.objects.*;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.objects.misc.FConnection;
import nijakow.four.server.runtime.objects.standard.FClosure;
import nijakow.four.server.runtime.objects.standard.FString;
import nijakow.four.server.storage.StorageManager;
import nijakow.four.server.users.IdentityDatabase;
import nijakow.four.server.users.User;
import nijakow.four.server.runtime.vm.fiber.Fiber;
import nijakow.four.server.process.Process;
import nijakow.four.share.lang.c.parser.StreamPosition;
import nijakow.four.share.util.ComparablePair;

import java.io.FileOutputStream;
import java.util.*;

public class VM {
	private final Logger logger;
	private final IdentityDatabase identityDB;
	private final NVFileSystem fs;
	private final StorageManager storageManager;
	private final Server server;
	private final Queue<Fiber> fibers = new LinkedList<>();
	private final PriorityQueue<ComparablePair<Long, Callback>> pendingCallbacks = new PriorityQueue<>();
	private Callback errorCallback = null;
	
	public VM(Logger logger, IdentityDatabase identityDB, NVFileSystem fs, StorageManager storageManager, Server server) {
		this.logger = logger;
		this.identityDB = identityDB;
		this.fs = fs;
		this.storageManager = storageManager;
		this.server = server;
		getLogger().println(LogLevel.INFO,"Initialized VM");
	}

	public Logger getLogger() { return logger; }
	public IdentityDatabase getIdentityDB() { return identityDB; }
	public NVFileSystem getFilesystem() {
		return fs;
	}
	public StorageManager getStorageManager() { return storageManager; }
	
	public Callback createCallback(Process state, FClosure closure) {
		return new Callback(this, state, closure);
	}
	
	public Callback createCallback(Process state, Blue subject, Key message) {
		return new Callback(this, state, subject, message);
	}

	public void setConnectCallback(Callback callback) {
		logger.println(LogLevel.DEBUG, "Connect callback was set to " + callback);
		this.server.onConnect((theConnection) -> {
			logger.println(LogLevel.DEBUG, "Connect callback was invoked (" + callback + ")");
			try {
				callback.invoke(new FConnection(theConnection));
			} catch (FourRuntimeException e) {
				logger.printException(e);
			}
		});
	}

	public void setErrorCallback(Callback callback) {
		this.errorCallback = callback;
	}

	public void reportError(String type, String name, String message) throws FourRuntimeException {
		if (this.errorCallback != null)
			this.errorCallback.invoke(new FString(type), new FString(name), new FString(message));
	}
	
	public long notificationWish() {
		long time = System.currentTimeMillis();
		if (pendingCallbacks.isEmpty())
			return 1;
		else {
			long diff = pendingCallbacks.peek().getFirst() - time;
			if (diff < 0)
				return 0;
			else
				return diff;
		}
	}

	private void wakeCallbacks() throws FourRuntimeException {
		long time = System.currentTimeMillis();
		
		while (!pendingCallbacks.isEmpty() && pendingCallbacks.peek().getFirst() <= time) {
			pendingCallbacks.poll().getSecond().invoke();
		}
	}
	
	private void runAllActiveFibers() throws FourRuntimeException {
		while (!fibers.isEmpty()) {			
			int x = 0;
			Fiber fiber = fibers.poll();
			try {
				User user = fiber.getSharedState().getUser();
				if (user != null)
					user.notifyActive();
				while (!fiber.isTerminated()) {
					if (x >= 10000000) {
						throw new FourRuntimeException("Process timed out!");
					}
					fiber.tick();
					x++;
				}
			} catch (FourRuntimeException e) {
				StreamPosition lastTell = fiber.getLastTell();
				if (lastTell != null) {
					logger.println(LogLevel.ERROR, "Caught " + e.getClass());
					logger.println(LogLevel.ERROR, "Execution context:");
					logger.println(LogLevel.ERROR, fiber.getLastTell().makeErrorText(e.getMessage()));
				} else {
					logger.printException(e);
					logger.println(LogLevel.INFO, "The exception was caught, the VM will continue to run.");
				}
				reportError("four", e.getClass().getName(), e.getMessage());
			}
		}
	}
	
	public void tick() throws FourRuntimeException {
		wakeCallbacks();
		runAllActiveFibers();
	}
	
	public Fiber spawnFiber() {
		return new Fiber(this);
	}

	public Fiber spawnFiber(Process state) {
		if (state == null) return spawnFiber();
		else return new Fiber(this, state);
	}
	
	public void startFiber(Blue self, Key key, Instance[] args) throws FourRuntimeException {
		Fiber fiber = spawnFiber();
		for (Instance arg : args)
			fiber.push(arg);
		self.send(fiber, key, args.length);
		fibers.add(fiber);
	}
	
	public void startFiber(Blue self, Key key) throws FourRuntimeException {
		startFiber(self, key, new Instance[0]);
	}
	
	public Fiber startFiber(Process state, FClosure closure, Instance[] args) throws FourRuntimeException {
		Fiber fiber = spawnFiber(state);
		for (Instance arg : args)
			fiber.push(arg);
		closure.invoke(fiber, args.length);
		fibers.add(fiber);
		return fiber;
	}
	
	public void invokeIn(Process state, Blue subject, Key message, long millis) {
		long time = System.currentTimeMillis();
		pendingCallbacks.add(new ComparablePair<>(time + millis, createCallback(state, subject, message)));
	}

	public void invokeIn(Process state, FClosure closure, long millis) {
		long time = System.currentTimeMillis();
		pendingCallbacks.add(new ComparablePair<>(time + millis, new Callback(this, state, closure)));
	}
}
