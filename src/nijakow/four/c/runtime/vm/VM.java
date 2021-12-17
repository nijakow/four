package nijakow.four.c.runtime.vm;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.FConnection;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.fs.Filesystem;
import nijakow.four.net.Server;
import nijakow.four.util.ComparablePair;

public class VM {
	private final Filesystem fs;
	private final Server server;
	private final Queue<Fiber> fibers = new LinkedList<>();
	private final PriorityQueue<ComparablePair<Long, Callback>> pendingCallbacks = new PriorityQueue<>();
	
	
	public VM(Filesystem fs, Server server) {
		this.fs = fs;
		this.server = server;
	}
	
	public Filesystem getFilesystem() {
		return fs;
	}
	
	public Callback createCallback(Blue subject, Key message) {
		return new Callback(this, subject, message);
	}

	public void setConnectCallback(Callback callback) {
		this.server.onConnect((theConnection) -> callback.invoke(new FConnection(theConnection)));
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

	private void wakeCallbacks() {
		long time = System.currentTimeMillis();
		
		while (!pendingCallbacks.isEmpty() && pendingCallbacks.peek().getFirst() <= time) {
			pendingCallbacks.poll().getSecond().invoke();
		}
	}
	
	private void runAllActiveFibers() {
		while (!fibers.isEmpty()) {			
			int x = 0;
			Fiber fiber = fibers.poll();
			while (!fiber.isTerminated()) {
				if (x >= 10000000) {
					// TODO: Throw an error!
					break;
				}
				fiber.tick();
				x++;
			}
		}
	}
	
	public void tick() {
		wakeCallbacks();
		runAllActiveFibers();
	}
	
	public Fiber spawnFiber() {
		return new Fiber(this);
	}
	
	public void startFiber(Blue self, Key key, Instance[] args) {
		Fiber fiber = spawnFiber();
		for (Instance arg : args)
			fiber.push(arg);
		self.send(fiber, key, args.length);
		fibers.add(fiber);
	}
	
	public void startFiber(Blue self, Key key) {
		startFiber(self, key, new Instance[0]);
	}
	
	public void invokeIn(Blue subject, Key message, long millis) {
		long time = System.currentTimeMillis();
		pendingCallbacks.add(new ComparablePair<>(time + millis, createCallback(subject, message)));
	}
}
