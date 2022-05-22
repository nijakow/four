package nijakow.four.smalltalk;

import nijakow.four.smalltalk.vm.Fiber;

import java.util.ArrayList;
import java.util.List;

public class SmalltalkVM {
    private final List<Fiber> runningSet = new ArrayList<Fiber>();

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
    public void tick() {
        for (int i = 0; i < runningSet.size(); i++) {
            final Fiber fiber = runningSet.get(i);
            fiber.runForAWhile();
        }
    }

    public void restartFiber(Fiber fiber) { runningSet.add(fiber); }
    public void pauseFiber(Fiber fiber) { runningSet.remove(fiber); }
}
