package nijakow.four.server.runtime.vm;

import java.util.Stack;

import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.vm.code.ByteCode;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.Instance;

public class Fiber {
	private final VM vm;
	private final SharedFiberState state;
	private Instance accumulator;
	private final Stack<Instance> stack = new Stack<>();
	private Frame top = null;

	Fiber(VM vm) { this(vm, new SharedFiberState(vm)); }
	Fiber(VM vm, SharedFiberState state) {
		this.vm = vm;
		this.state = state;
	}
	
	public VM getVM() { return vm; }

	public SharedFiberState getSharedState() { return state; }
	
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
		int varargCount = args - code.getFixedArgCount();
		Frame f = new Frame(top, code, self);
		while (varargCount --> 0) {
			f.addVararg(pop());
			args--;
		}
		setTop(f);
		while (args --> 0) {
			top.setLocal(args, pop());
		}
	}
	
	public boolean isTerminated() {
		return top == null;
	}
	
	public void tick() throws FourRuntimeException {
		top.tick(this);
	}
}
