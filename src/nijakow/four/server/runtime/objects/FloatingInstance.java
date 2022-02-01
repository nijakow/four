package nijakow.four.server.runtime.objects;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class FloatingInstance extends Instance {
    private static final Set<FloatingInstance> instances;

    static {
        instances = Collections.newSetFromMap(new WeakHashMap<>());
    }

    protected void registerToPool() {
        instances.add(this);
    }
}
