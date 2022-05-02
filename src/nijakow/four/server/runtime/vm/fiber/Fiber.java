package nijakow.four.server.runtime.vm.fiber;

import java.util.Stack;

import nijakow.four.server.process.Process;
import nijakow.four.server.runtime.exceptions.CastException;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.server.runtime.vm.VM;
import nijakow.four.server.runtime.vm.code.ByteCode;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class Fiber {
	private final VM vm;
	private final Process state;
	private Instance accumulator;
	private final Stack<Instance> stack = new Stack<>();
	private Frame top = null;
	private StreamPosition lastTell = null;

	public Fiber(VM vm) { this(vm, new Process(vm)); }
	public Fiber(VM vm, Process state) {
		this.vm = vm;
		this.state = state;
	}
	
	public VM getVM() { return vm; }

	public Process getSharedState() { return state; }
	
	public Instance getAccu() {
		return accumulator;
	}

	public void setAccu(Instance value) {
		accumulator = value;
	}

	public StreamPosition getLastTell() {
		return this.lastTell;
	}

	public void setLastTell(StreamPosition tell) {
		this.lastTell = tell;
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

	public void enter(Blue self, ByteCode code, int args) throws FourRuntimeException {
		if (!code.argCheck(args))
			throw new FourRuntimeException("Ouch! Arg error!");
		int varargCount = args - code.getFixedArgCount();
		Frame f = new Frame(top, code, self);
		while (varargCount --> 0) {
			f.addVararg(pop());
			args--;
		}
		setTop(f);
		Type[] argTypes = code.getMeta().getArgTypes();
		while (args --> 0) {
			Instance arg = pop();
			if (!argTypes[args].check(arg))
				throw new CastException(argTypes[args], arg);
			top.setLocal(args, arg);
		}
	}
	
	public boolean isTerminated() {
		return top == null;
	}
	
	public void tick() throws FourRuntimeException {
		top.tick(this);
	}

    public boolean isRoot() {
		return getSharedState().getUser() != null && vm.getIdentityDB().getRootUser() == getSharedState().getUser();
    }
}
