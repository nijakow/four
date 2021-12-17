package nijakow.four.c.runtime;

public class FString extends Instance {
	private final String value;
	
	public FString(String value) {
		this.value = value;
	}
	
	@Override
	public boolean asBoolean() {
		return !value.isEmpty();
	}
	
	@Override
	public String asString() {
		return value;
	}
}
