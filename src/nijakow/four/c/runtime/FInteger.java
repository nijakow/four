package nijakow.four.c.runtime;

public class FInteger extends Instance {
	private final int value;
	
	public FInteger(int value) {
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		else if (!(obj instanceof FInteger)) return false;
		else return ((FInteger) obj).value == value;
	}

	@Override
	public boolean asBoolean() {
		return value != 0;
	}

	@Override
	public int asInt() {
		return value;
	}
	
	@Override
	public String asString() {
		return Integer.toString(value);
	}
	
	@Override
	public Instance plus(Instance y) {
		return new FInteger(value + y.asInt());
	}
}
