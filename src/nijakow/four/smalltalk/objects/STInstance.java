package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.vm.FourException;

public abstract class STInstance {
    public abstract STClass getClass(World world);
    public STMethod getInstanceMethod(World world, STSymbol name) {
        return getClass(world).findMethod(name);
    }

    public boolean is(Object obj) {
        return this == obj;
    }

    private void castError() throws FourException { throw new FourException("Can not cast!"); }
    public STArray asArray() throws FourException { castError(); return null; }
    public STBoolean asBoolean() throws FourException { castError(); return null; }
    public STCharacter asCharacter() throws FourException { castError(); return null; }
    public STClass asClass() throws FourException { castError(); return null; }
    public STClosure asClosure() throws FourException { castError(); return null; }
    public STInteger asInteger() throws FourException { castError(); return null; }
    public STNil asNil() throws FourException { castError(); return null; }
    public STObject asObject() throws FourException { castError(); return null; }
    public STPort asPort() throws FourException { castError(); return null; }
    public STString asString() throws FourException { castError(); return null; }
    public STSymbol asSymbol() throws FourException { castError(); return null; }
    public STCompiledMethod asCompiledMethod() throws FourException { castError(); return null; }

    public boolean isTrue() {
        return this != STBoolean.getFalse();
    }
}
