package nijakow.four.smalltalk.vm.instructions;

import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.FourException;

public abstract class VMInstruction {
    private VMInstruction next;

    protected VMInstruction() {
    }

    public abstract VMInstruction run(Fiber fiber) throws FourException;

    public VMInstruction getNext() { return this.next; }
    public void setNext(VMInstruction next) {
        this.next = next;
    }
}
