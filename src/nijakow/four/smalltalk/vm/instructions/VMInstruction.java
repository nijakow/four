package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;

public abstract class VMInstruction {
    private VMInstruction next;

    protected VMInstruction() {
    }

    public abstract void run(Fiber fiber);

    public VMInstruction getNext() { return this.next; }
    public void setNext(VMInstruction next) {
        this.next = next;
    }
}
