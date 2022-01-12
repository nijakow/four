package nijakow.four.runtime.vm;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import nijakow.four.runtime.*;
import nijakow.four.runtime.fs.Filesystem;
import nijakow.four.net.Server;
import nijakow.four.util.ComparablePair;

public class VM {
	private final Filesystem fs;
	private final Server server;
	private final Queue<Fiber> fibers = new LinkedList<>();
	private final PriorityQueue<ComparablePair<Long, Callback>> pendingCallbacks = new PriorityQueue<>();
	private Callback errorCallback = null;
	
	public VM(Filesystem fs, Server server) {
		this.fs = fs;
		this.server = server;
	}
	
	public Filesystem getFilesystem() {
		return fs;
	}
	
	public Callback createCallback(FClosure closure) {
		return new Callback(this, closure);
	}
	
	public Callback createCallback(Blue subject, Key message) {
		return new Callback(this, subject, message);
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
	
	public void startFiber(FClosure closure, Instance[] args) throws FourRuntimeException {
		Fiber fiber = spawnFiber();
		for (Instance arg : args)
			fiber.push(arg);
		closure.invoke(fiber, args.length);
		fibers.add(fiber);
	}
	
	public void invokeIn(Blue subject, Key message, long millis) {
		long time = System.currentTimeMillis();
		pendingCallbacks.add(new ComparablePair<>(time + millis, createCallback(subject, message)));
	}
}
