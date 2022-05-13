package nijakow.four.server.runtime.types;

import nijakow.four.server.runtime.exceptions.CastException;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.objects.blue.Blueprint;

import java.util.HashMap;
import java.util.Map;

public class ObjectType extends Type {
    private final String blueprint;

    public ObjectType() { this(null); }

    private ObjectType(String blueprint) {
        this.blueprint = blueprint;
    }

    public String getName() { return (blueprint == null) ? "object" : ("object(" + blueprint + ")"); }

    @Override
    public Instance cast(Instance instance) throws CastException {
        if (check(instance))
            return instance;
        else
            return instance.asBlue();
    }

    @Override
    public boolean check(Instance instance) {
        if (instance.isNil()) return true;
        return (instance instanceof Blue) && (blueprint == null || ((Blue) instance).getBlueprint().extendsBlueprint(blueprint));
    }

    private static final Map<String, ObjectType> map = new HashMap<>();
    public static ObjectType get(String blueprint) {
        if (!map.containsKey(blueprint))
            map.put(blueprint, new ObjectType(blueprint));
        return map.get(blueprint);
    }
}
