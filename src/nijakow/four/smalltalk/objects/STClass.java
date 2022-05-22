package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.compiler.STCompiler;

import java.util.HashMap;
import java.util.Map;

public class STClass extends STInstance {
    private final STSymbol[] members;
    private final Map<STSymbol, STMethod> methods;

    public STClass(STSymbol[] members) {
        this.members = members;
        this.methods = new HashMap<>();
    }

    public STCompiler openCompiler() {
        return new STCompiler(this, null);
    }

    public int findMember(STSymbol name) {
        for (int i = 0; i < members.length; i++)
            if (members[i] == name)
                return i;
        return -1;
    }
}
