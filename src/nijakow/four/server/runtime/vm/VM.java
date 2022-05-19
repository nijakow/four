package nijakow.four.server.runtime.vm;

import nijakow.four.server.Four;
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
	private final Four four;
	private final ArrayList<Fiber> fibers = new ArrayList<>();
	private final PriorityQueue<ComparablePair<Long, Fiber>> sleepingFibers = new PriorityQueue<>();
	private Callback errorCallback = null;
	
	public VM(Four four) {
		this.four = four;
		getLogger().println(LogLevel.INFO,"Initialized VM");
	}

	public Four getFour() { return this.four; }
	public Logger getLogger() { return getFour().getLogger(); }
	public IdentityDatabase getIdentityDB() { return getFour().getIdentityDB(); }
	public NVFileSystem getFilesystem() { return getFour().getFilesystem(); }
	public StorageManager getStorageManager() { return getFour().getStorageManager(); }
	private Server getServer() { return getFour().getServer(); }
	
	public Callback createCallback(Process state, FClosure closure) {
		return new Callback(this, state, closure);
	}
	
	public Callback createCallback(Process state, Blue subject, Key message) {
		return new Callback(this, state, subject, message);
	}

	public void setConnectCallback(Callback callback) {
		this.getServer().onConnect((theConnection) -> {
			try {
				callback.invoke(new FConnection(theConnection));
			} catch (FourRuntimeException e) {
				getLogger().printException(e);
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

	public void pauseFiber(Fiber fiber) {
		fibers.remove(fiber);
	}

	public void restartFiber(Fiber fiber) {
		fibers.add(fiber);
	}
	
	public long notificationWish() {
		long time = System.currentTimeMillis();
		if (sleepingFibers.isEmpty())
			return 1;
		else {
			long diff = sleepingFibers.peek().getFirst() - time;
			if (diff < 0)
				return 0;
			else
				return diff;
		}
	}

	private void wakeCallbacks() throws FourRuntimeException {
		long time = System.currentTimeMillis();
		
		while (!sleepingFibers.isEmpty() && sleepingFibers.peek().getFirst() <= time) {
			sleepingFibers.poll().getSecond().restart();
		}
	}

	private void runFiber(Fiber fiber) throws FourRuntimeException {
		int ticks = 0;
		try {
			User user = fiber.getSharedState().getUser();
			if (user != null)
				user.notifyActive();
			while (!fiber.isTerminated() && !fiber.isPaused()) {
				if (ticks >= 100000) {
					return;
				}
				fiber.tick();
				ticks++;
			}
		} catch (FourRuntimeException e) {
			fiber.terminate(Instance.getNil());
			StreamPosition lastTell = fiber.getLastTell();
			if (lastTell != null) {
				getLogger().println(LogLevel.ERROR, "Caught " + e.getClass());
				getLogger().println(LogLevel.ERROR, "Execution context:");
				getLogger().println(LogLevel.ERROR, fiber.getLastTell().makeErrorText(e.getMessage()));
			} else {
				getLogger().printException(e);
				getLogger().println(LogLevel.INFO, "The exception was caught, the VM will continue to run.");
			}
			reportError("four", e.getClass().getName(), e.getMessage());
		}
	}

	private void runAllActiveFibers() throws FourRuntimeException {
		Fiber[] fibers = this.fibers.toArray(new Fiber[]{});
		for (Fiber fiber : fibers) {
			runFiber(fiber);
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

	public void sleepFiber(Fiber fiber, long millis) {
		long time = System.currentTimeMillis();
		sleepingFibers.add(new ComparablePair<>(time + millis, fiber));
	}
}
