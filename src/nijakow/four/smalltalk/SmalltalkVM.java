package nijakow.four.smalltalk;

import nijakow.four.smalltalk.objects.STInstance;
import nijakow.four.smalltalk.objects.STSymbol;
import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.FourException;

import java.util.ArrayList;
import java.util.List;

public class SmalltalkVM {
    private final List<Fiber> runningSet = new ArrayList<Fiber>();
    private final FourSmalltalk four;
    private final World world;

    public SmalltalkVM(FourSmalltalk four, World world) {
        this.four = four;
        this.world = world;
    }

    public FourSmalltalk getFour() { return this.four; }
    public World getWorld() { return this.world; }

    /**
     * Get the time that the VM wants to spend in idle mode.
     * @return The desired idle time in milliseconds. -1 for any value.
     */
    public long getPreferredIdleTime() {
        return (runningSet.isEmpty()) ? -1 : 0;
    }

    /**
     * Tick the VM.
     */
    public void tick() throws FourException {
        for (int i = 0; i < runningSet.size(); i++) {
            final Fiber fiber = runningSet.get(i);
            fiber.runForAWhile();
        }
    }

    public Fiber startFiber(STInstance self, STSymbol message, STInstance[] args) throws FourException {
        Fiber fiber = new Fiber(this);
        fiber.enter(self, message, args);
        fiber.restart();
        return fiber;
    }
    public Fiber startFiber(STInstance self, String message, STInstance[] args) throws FourException {
        return startFiber(self, STSymbol.get(message), args);
    }

    public void restartFiber(Fiber fiber) { runningSet.add(fiber); }
    public void pauseFiber(Fiber fiber) { runningSet.remove(fiber); }
}
