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
import nijakow.four.share.lang.base.parser.StreamPosition;

public class Fiber {
	private final VM vm;
	private final Process state;
	private Instance accumulator;
	private final Stack<Instance> stack = new Stack<>();
	private Frame top = null;
	private StreamPosition lastTell = null;
	private boolean isPaused = false;
	private Fiber returnTo;

	public Fiber(VM vm) { this(vm, new Process(vm)); }
	public Fiber(VM vm, Process state) {
		this.vm = vm;
		this.state = state;
	}
	
	public VM getVM() { return vm; }

	public Process getSharedState() { return state; }

	public boolean isPaused() { return this.isPaused; }
	public void restart() {
		if (isPaused()) {
			isPaused = false;
			getVM().restartFiber(this);
		}
	}

	public void pause() {
		if (!isPaused()) {
			isPaused = true;
			getVM().pauseFiber(this);
		}
	}

	public void sleep(long millis) {
		if (!isPaused()) {
			isPaused = true;
			getVM().sleepFiber(this, millis);
		}
	}

	public void restartWithValue(Instance value) {
		if (isPaused()) {
			setAccu(value);
			restart();
		}
	}

	public void terminate(Instance value) {
		pause();
		if (this.returnTo != null)
			this.returnTo.restartWithValue(value);
	}

	public void returnTo(Fiber fiber) {
		this.returnTo = fiber;
	}

	public void terminate() {
		terminate(getAccu());
	}
	
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

	public void nonlocalExit(Frame frame) {
		while (top != null) {
			Frame lastFrame = top;
			setTop(top.getPrevious());
			if (lastFrame == frame)
				break;
		}
	}
	
	public boolean isTerminated() {
		return top == null;
	}
	
	public void tick() throws FourRuntimeException {
		top.tick(this);
		if (top == null)
			terminate();
	}

    public boolean isRoot() {
		return getSharedState().getUser() != null && vm.getIdentityDB().getRootUser() == getSharedState().getUser();
	}
}
