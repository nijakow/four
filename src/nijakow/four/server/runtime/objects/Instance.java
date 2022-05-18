package nijakow.four.server.runtime.objects;

import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.objects.collections.FList;
import nijakow.four.server.runtime.objects.collections.FMapping;
import nijakow.four.server.runtime.objects.misc.FConnection;
import nijakow.four.server.runtime.objects.standard.FClosure;
import nijakow.four.server.runtime.objects.standard.FString;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.server.runtime.exceptions.CastException;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.vm.fiber.Fiber;
import nijakow.four.server.runtime.vm.VM;

public abstract class Instance {
	private static final Instance NIL = new Instance() {
		@Override
		public String toString() {
			return "nil";
		}

		@Override
		public String getType() { return "nil"; }
	};

	public static Instance getNil() { return NIL; }
	
	public boolean isNil() { return this == NIL; }

	public String getType() { return "unknown"; }

	public boolean asBoolean() { return !isNil(); }
	public int asInt() { return hashCode(); }
	public String asString() { return toString(); }
	public Key asKey() throws FourRuntimeException { throw new FourRuntimeException("Conversion failed:" + this + " to Key!"); }
	public Blue asBlue() throws CastException { throw new CastException(Type.getObject(), this); }
	public FString asFString() throws CastException { throw new CastException(Type.getString(), this); }
	public FList asFList() throws CastException { throw new CastException(Type.getList(), this); }
	public FMapping asFMapping() throws CastException { throw new CastException(Type.getMapping(), this); }
	public FClosure asFClosure() throws CastException { throw new CastException(Type.getFunc(), this); }
	public FConnection asFConnection() throws FourRuntimeException { throw new FourRuntimeException("Conversion failed:" + this + " to FConnection!"); }
	
	public Instance plus(Instance y) throws FourRuntimeException { throw new FourRuntimeException("Addition failed!"); }
	
	public Instance index(Instance index) throws FourRuntimeException { throw new FourRuntimeException("Invalid index!"); }
	public Instance putIndex(Instance index, Instance value) throws FourRuntimeException { throw new FourRuntimeException("Invalid index!"); }
	public int length() { return 0; }

	public void invoke(Fiber fiber, int args) throws FourRuntimeException {
		throw new FourRuntimeException("Oof. Can't invoke this object: " + this);
	}
	
	public void loadSlot(Fiber fiber, Key key) throws FourRuntimeException {
		throw new FourRuntimeException("Oof. Slot not found: " + key + " in " + this);
	}
	
	public void storeSlot(Fiber fiber, Key key, Instance value) throws FourRuntimeException {
		throw new FourRuntimeException("Oof. Slot not found: " + key + " in " + this);
	}
	
	public void send(Fiber fiber, Key key, int args) throws FourRuntimeException {
		throw new FourRuntimeException("Oof. Method not found: " + key + " in " + this);
	}
	
	public Code extractMethod(VM vm, Key key) throws FourRuntimeException {
		throw new FourRuntimeException("Oof. Can't extract method in " + this);
	}
}
