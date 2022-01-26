package nijakow.four.server.runtime.objects.blue;

import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.lang.c.SlotVisibility;

public class Slot {
    private final SlotVisibility visibility;
    private final Type type;
    private final Key name;
    private Instance value;

    public Slot(SlotVisibility visibility, Type type, Key name, Instance value) {
        this.visibility = visibility;
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Slot(SlotVisibility visibility, Type type, Key name) {
        this(visibility, type, name, Instance.getNil());
    }

    public SlotVisibility getVisibility() { return visibility; }
    public Type getType() { return type; }
    public Key getName() { return name; }
    public Instance getValue() { return value; }
    public void setValue(Instance value) { this.value = value; }
}
