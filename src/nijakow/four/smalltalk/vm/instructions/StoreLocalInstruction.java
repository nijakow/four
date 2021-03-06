package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;

public class StoreLocalInstruction extends VMInstruction {
    private final int depth;
    private final int offset;

    public StoreLocalInstruction(int depth, int offset) {
        this.depth = depth;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "STORE_LOCAL " + depth + ", " + offset;
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        fiber.storeVariable(depth, offset);
        return getNext();
    }
}
