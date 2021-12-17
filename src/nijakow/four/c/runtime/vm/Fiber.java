package nijakow.four.c.runtime.vm;

import java.util.Stack;

import nijakow.four.c.runtime.Blue;
import nijakow.four.c.runtime.ByteCode;
import nijakow.four.c.runtime.Instance;

public class Fiber {
	private final VM vm;
	private Instance accumulator;
	private Stack<Instance> stack = new Stack<>();
	private Frame top = null;

	Fiber(VM vm) { this.vm = vm; }
	
	public VM getVM() { return vm; }
	
	public Instance getAccu() {
		return accumulator;
	}

	public void setAccu(Instance value) {
		accumulator = value;
	}

	public void push(Instance value) {
		stack.push(value);
	}
	
	public Instance pop() {
		return stack.pop();
	}

	public void setTop(Frame frame) {
		top = frame;
	}

	public void enter(Blue self, ByteCode code, int args) {
		if (!code.argCheck(args))
			throw new RuntimeException("Ouch! Arg error!");
		setTop(new Frame(top, code, self));
		while (args --> 0) {
			top.setLocal(args, pop());
		}
	}
	
	public boolean isTerminated() {
		return top == null;
	}
	
	public void tick() {
		top.tick(this);
	}
}
