package nijakow.four.c.runtime;

public abstract class Type {
	private static final Type ANY = new Type() {
		
		@Override
		public Instance cast(Instance instance) {
			return instance;
		}
		
		@Override
		public boolean check(Instance instance) {
			return true;
		}
	};
	
	private static final Type VOID = new Type() {
		
		@Override
		public Instance cast(Instance instance) {
			return Instance.getNil();
		}
		
		@Override
		public boolean check(Instance instance) {
			return instance.isNil();
		}
	};
	
	private static final Type INT = new Type() {
		
		@Override
		public Instance cast(Instance instance) {
			if (check(instance))
				return instance;
			else
				return new FInteger(instance.asInt());
		}
		
		@Override
		public boolean check(Instance instance) {
			return instance instanceof FInteger;
		}
	};
	
	
	public static Type getAny() { return ANY; }
	public static Type getVoid() { return VOID; }
	public static Type getInt() { return INT; }
	
	private Type() {}
	
	
	public abstract Instance cast(Instance instance);
	
	public abstract boolean check(Instance instance);
	
	public void expect(Instance instance) {
		if (!check(instance))
			throw new RuntimeException("Expected different type!");
	}
}
