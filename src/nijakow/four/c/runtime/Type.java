package nijakow.four.c.runtime;

public abstract class Type {
	private static final Type ANY = new Type() {
		
		@Override
		public Instance cast(Instance instance) {
			return instance;
		}
		
		@Override
		public boolean check(Instance i) {
			return true;
		}
	};
	
	
	public static Type getAny() { return ANY; }
	
	private Type() {}
	
	
	public abstract Instance cast(Instance instance);
	
	public abstract boolean check(Instance instance);
	
	public void expect(Instance instance) {
		if (!check(instance))
			throw new RuntimeException("Expected different type!");
	}
}
