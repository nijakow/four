package nijakow.four.c.runtime.vm;

import java.util.LinkedList;
import java.util.Queue;

import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.FConnection;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;
import nijakow.four.net.Server;

public class VM {
	private final Server server;
	private final Queue<Fiber> fibers = new LinkedList<>();
	
	public VM(Server server) {
		this.server = server;
	}
	
	public Callback createCallback(Blue subject, Key message) {
		return new Callback(this, subject, message);
	}

	public void setConnectCallback(Callback callback) {
		this.server.onConnect((theConnection) -> callback.invoke(new FConnection(theConnection)));
	}
	
	public double notificationWish() {
		return fibers.isEmpty() ? 1 : 0;
	}

	public void tick() {
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
}
