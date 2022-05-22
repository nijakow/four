package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;

public class LoadLocalInstruction extends VMInstruction {
    private final int depth;
    private final int offset;

    public LoadLocalInstruction(int depth, int offset) {
        this.depth = depth;
        this.offset = offset;
    }

    @Override
    public VMInstruction run(Fiber fiber) {
        fiber.loadVariable(depth, offset);
        return getNext();
    }
}
