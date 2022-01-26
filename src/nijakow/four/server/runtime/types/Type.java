package nijakow.four.server.runtime.types;

import nijakow.four.server.runtime.exceptions.CastException;
import nijakow.four.server.runtime.objects.*;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.objects.collections.FMapping;
import nijakow.four.server.runtime.objects.standard.FClosure;
import nijakow.four.server.runtime.objects.standard.FInteger;
import nijakow.four.server.runtime.objects.standard.FString;

public abstract class Type {
	private static final Type ANY = new Type() {

		public String getName() { return "any"; }
		
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

		public String getName() { return "void"; }
		
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

		public String getName() { return "int"; }
		
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

		public String getName() { return "string"; }
		
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

		public String getName() { return "object"; }
		
		@Override
		public Instance cast(Instance instance) throws CastException {
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

		public String getName() { return "func"; }
		
		@Override
		public Instance cast(Instance instance) throws CastException {
			if (check(instance))
				return instance;
			else
				throw new CastException(this, instance);
		}
		
		@Override
		public boolean check(Instance instance) {
			return (instance instanceof FClosure) || instance.isNil();
		}
	};
	
	private static final Type MAPPING = new Type() {

		public String getName() { return "mapping"; }
		
		@Override
		public Instance cast(Instance instance) throws CastException {
			if (check(instance))
				return instance;
			else
				throw new CastException(this, instance);
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
	public static ListType getList() { return ANY.listType(); }
	public static Type getMapping() { return MAPPING; }
	
	
	private ListType theListType = null;
	
	protected Type() {}

	@Override
	public String toString() {
		return "c_type(" + getName() + ")";
	}

	public abstract String getName();

	public abstract Instance cast(Instance instance) throws CastException;
	
	public abstract boolean check(Instance instance);
	
	public void expect(Instance instance) throws CastException {
		if (!check(instance))
			throw new CastException(this, instance);
	}
	
	public ListType listType() {
		if (theListType == null)
			theListType = new ListType(this);
		return theListType;
	}
}
