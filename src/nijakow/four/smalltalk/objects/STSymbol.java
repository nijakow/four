package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.vm.Fiber;
import nijakow.four.smalltalk.vm.FourException;
import nijakow.four.smalltalk.vm.instructions.VMInstruction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class STSymbol extends STInstance {
    private final String name;

    private STSymbol(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "#'" + getName() + "'";
    }

    @Override
    public STSymbol asSymbol() throws FourException {
        return this;
    }

    public String getName() { return this.name; }

    public boolean isProbablyAGlobal() {
        return Character.isUpperCase(getName().charAt(0));
    }

    private static Map<String, STSymbol> symbols = new HashMap<>();
    public static STSymbol get(String name) {
        if (!symbols.containsKey(name))
            symbols.put(name, new STSymbol(name));
        return symbols.get(name);
    }

    @Override
    public STClass getClass(World world) {
        return world.getSymbolClass();
    }

    public Consumer<Fiber> getBuiltin(World world) {
        return world.getBuiltinFor(this);
    }
}
