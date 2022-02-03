package nijakow.four.server.runtime.vm;

import nijakow.four.server.net.Server;
import nijakow.four.server.runtime.*;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.nvfs.NVFileSystem;
import nijakow.four.server.runtime.objects.*;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.objects.misc.FConnection;
import nijakow.four.server.runtime.objects.standard.FClosure;
import nijakow.four.server.runtime.objects.standard.FString;
import nijakow.four.server.runtime.security.users.IdentityDatabase;
import nijakow.four.share.util.ComparablePair;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class VM {
	private final IdentityDatabase identityDB;
	private final NVFileSystem fs;
	private final Server server;
	private final Queue<Fiber> fibers = new LinkedList<>();
	private final PriorityQueue<ComparablePair<Long, Callback>> pendingCallbacks = new PriorityQueue<>();
	private Callback errorCallback = null;
	
	public VM(IdentityDatabase identityDB, NVFileSystem fs, Server server) {
		this.identityDB = identityDB;
		this.fs = fs;
		this.server = server;
	}

	public IdentityDatabase getIdentityDB() { return identityDB; }
	public NVFileSystem getFilesystem() {
		return fs;
	}
	
	public Callback createCallback(SharedFiberState state, FClosure closure) {
		return new Callback(this, state, closure);
	}
	
	public Callback createCallback(SharedFiberState state, Blue subject, Key message) {
		return new Callback(this, state, subject, message);
	}

	public void setConnectCallback(Callback callback) {
		this.server.onConnect((theConnection) -> {
			try {
				callback.invoke(new FConnection(theConnection));
			} catch (FourRuntimeException e) {
				e.printStackTrace(); // TODO: Handle this gracefully (use smth different than Consumer)
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
				while (!fiber.isTerminated()) {
					if (x >= 10000000) {
						throw new FourRuntimeException("Process timed out!");
					}
					fiber.tick();
					x++;
				}
			} catch (FourRuntimeException e) {
				e.printStackTrace();
				reportError("four", e.getClass().getName(), e.getMessage());
				System.out.println("The exception was caught, the VM will continue to run.");
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

	public Fiber spawnFiber(SharedFiberState state) {
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
	
	public void startFiber(SharedFiberState state, FClosure closure, Instance[] args) throws FourRuntimeException {
		Fiber fiber = spawnFiber(state);
		for (Instance arg : args)
			fiber.push(arg);
		closure.invoke(fiber, args.length);
		fibers.add(fiber);
	}
	
	public void invokeIn(SharedFiberState state, Blue subject, Key message, long millis) {
		long time = System.currentTimeMillis();
		pendingCallbacks.add(new ComparablePair<>(time + millis, createCallback(state, subject, message)));
	}
}
