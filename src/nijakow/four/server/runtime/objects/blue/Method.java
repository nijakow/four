package nijakow.four.server.runtime.objects.blue;

import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.vm.code.Code;
import nijakow.four.share.lang.c.SlotVisibility;

public class Method {
    private final SlotVisibility visibility;
    private final Key name;
    private final Code code;

    public Method(SlotVisibility visibility, Key name, Code code) {
        this.visibility = visibility;
        this.name = name;
        this.code = code;
    }

    public SlotVisibility getVisibility() { return visibility; }
    public Key getName() { return name; }
    public Code getCode() { return code; }
}
