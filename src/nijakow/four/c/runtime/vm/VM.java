package nijakow.four.c.runtime.vm;

import java.util.ArrayList;
import java.util.List;

import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;

public class VM {
	private final List<Fiber> fibers = new ArrayList<>();
	
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
	
	public void startFiber(Blue self, Key key, Instance[] args) {
		Fiber fiber = new Fiber();
		for (Instance arg : args)
			fiber.push(arg);
		self.send(fiber, key, args.length);
		fibers.add(fiber);
	}
	
	public VM() {
		
	}
}
