package nijakow.four.server.runtime.objects;

import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.lang.c.SlotVisibility;

public class Slot {
    private final Type type;
    private final Key name;
    private Instance value;

    public Slot(SlotVisibility visibility, Type type, Key name, Instance value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public Slot(SlotVisibility visibility, Type type, Key name) {
        this(visibility, type, name, Instance.getNil());
    }
}
