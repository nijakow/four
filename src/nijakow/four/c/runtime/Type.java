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
			return (instance instanceof FInteger) || instance.isNil();
		}
	};
	
	private static final Type STRING = new Type() {
		
		@Override
		public Instance cast(Instance instance) {
			if (check(instance))
				return instance;
			else
				return new FString(instance.asString());
		}
		
		@Override
		public boolean check(Instance instance) {
			return (instance instanceof FString) || instance.isNil();
		}
	};
	
	private static final Type OBJECT = new Type() {
		
		@Override
		public Instance cast(Instance instance) {
			if (check(instance))
				return instance;
			else
				return instance.asBlue();
		}
		
		@Override
		public boolean check(Instance instance) {
			return (instance instanceof Blue) || instance.isNil();
		}
	};
	
	private static final Type FUNC = new Type() {
		
		@Override
		public Instance cast(Instance instance) {
			if (check(instance))
				return instance;
			else
				throw new RuntimeException("Can't cast!");
		}
		
		@Override
		public boolean check(Instance instance) {
			return (instance instanceof FClosure) || instance.isNil();
		}
	};
	
	private static final Type LIST = new Type() {
		
		@Override
		public Instance cast(Instance instance) {
			if (check(instance))
				return instance;
			else
				throw new RuntimeException("Can't cast!");
		}
		
		@Override
		public boolean check(Instance instance) {
			return (instance instanceof FList) || instance.isNil();
		}
	};
	
	private static final Type MAPPING = new Type() {
		
		@Override
		public Instance cast(Instance instance) {
			if (check(instance))
				return instance;
			else
				throw new RuntimeException("Can't cast!");
		}
		
		@Override
		public boolean check(Instance instance) {
			return (instance instanceof FMapping) || instance.isNil();
		}
	};
	
	
	public static Type getAny() { return ANY; }
	public static Type getVoid() { return VOID; }
	public static Type getInt() { return INT; }
	public static Type getChar() { return getInt(); }
	public static Type getBool() { return getInt(); }
	public static Type getString() { return STRING; }
	public static Type getObject() { return OBJECT; }
	public static Type getFunc() { return FUNC; }
	public static Type getList() { return LIST; }
	public static Type getMapping() { return MAPPING; }
	
	private Type() {}
	
	
	public abstract Instance cast(Instance instance);
	
	public abstract boolean check(Instance instance);
	
	public void expect(Instance instance) {
		if (!check(instance))
			throw new RuntimeException("Expected different type!");
	}
}
