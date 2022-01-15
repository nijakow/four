package nijakow.four.runtime;

import nijakow.four.runtime.vm.Fiber;
import nijakow.four.runtime.vm.VM;
import nijakow.four.serialization.base.ISerializable;
import nijakow.four.serialization.base.ISerializer;

public abstract class Instance implements ISerializable {
	private static final Instance NIL = new Instance() {
		@Override
		public String getSerializationClassID() {
			return "nil";
		}

		@Override
		public int getSerializationClassRevision() {
			return 0;
		}

		@Override
		public void serialize(ISerializer serializer) {
		}
	};

	public static Instance getNil() { return NIL; }
	
	public boolean isNil() { return this == NIL; }
	
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
	
	public Instance plus(Instance y) { throw new RuntimeException("Addition failed!"); }
	
	public Instance index(Instance index) { throw new RuntimeException("Invalid index!"); }
	public Instance putIndex(Instance index, Instance value) throws CastException { throw new RuntimeException("Invalid index!"); }
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

	@Override
	public String getSerializationClassID() {
		return "Instance";	// FIXME: This is just a dummy!
	}

	@Override
	public int getSerializationClassRevision() {
		return 0;	// FIXME: This is just a dummy!
	}

	@Override
	public void serialize(ISerializer serializer) {
		// FIXME: This is just a dummy!
	}
}
