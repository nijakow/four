package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.vm.FourException;

import java.util.function.Consumer;

public class STForeign extends STInstance {
    @Override
    public STClass getClass(World world) {
        return world.getForeignClass();
    }

    @Override
    public STForeign asForeign() throws FourException {
        return this;
    }

    @Override
    public boolean isForeign() {
        return true;
    }

    public void send(STSymbol message, STInstance[] args, Consumer<STInstance> result) {
        result.accept(STNil.get());
    }
}
