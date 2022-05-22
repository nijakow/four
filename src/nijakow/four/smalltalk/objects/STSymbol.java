package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;

import java.util.HashMap;
import java.util.Map;

public class STSymbol extends STInstance {
    private final String name;

    private STSymbol(String name) {
        this.name = name;
    }

    public String getName() { return this.name; }

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
}
