package nijakow.four.c.runtime.vm;

import java.util.ArrayList;
import java.util.List;

import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;

public class VM {
	private final List<Fiber> fibers = new ArrayList<>();
	
	public VM() {
		
	}

	public double notificationWish() {
		return fibers.isEmpty() ? 1 : 0;
	}

	public void tick() {
		int x = 0;
		while (x < fibers.size()) {
			if (fibers.get(x).isTerminated()) {
				fibers.remove(x);
			} else {
				fibers.get(x).tick();
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
