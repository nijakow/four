package nijakow.four.c.runtime;

public class FInteger extends Instance {
	private final int value;
	
	public FInteger(int value) {
		this.value = value;
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
}
