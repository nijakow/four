package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.objects.method.STMethod;
import nijakow.four.smalltalk.vm.FourException;

import java.util.*;

public abstract class STInstance {
    /*
     *     S t a t i c   P a r t
     */
    private static final Set<STInstance> allInstances;

    static {
        allInstances = Collections.newSetFromMap(new WeakHashMap<>());
    }

    public static final STInstance[] allInstancesOf(STClass clazz, World world) {
        List<STInstance> instances = new ArrayList<>();
        for (STInstance instance : allInstances) {
            if (instance.inheritsFrom(clazz, world))
                instances.add(instance);
        }
        return instances.toArray(new STInstance[]{});
    }

    /*
     *     D y n a m i c   P a r t
     */
    public STInstance() {
        register();
    }

    public abstract STClass getClass(World world);

    public boolean inheritsFrom(STClass clazz, World world) {
        STClass ourClass = getClass(world);
        return ourClass.isSubclassOf(clazz);
    }

    public STMethod getInstanceMethod(World world, STSymbol name) {
        return getClass(world).findMethod(name);
    }

    public boolean is(Object obj) {
        return this == obj;
    }

    public void register() {
        allInstances.add(this);
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
