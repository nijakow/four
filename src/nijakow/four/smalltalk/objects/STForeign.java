package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.net.IMUDConnection;
import nijakow.four.smalltalk.vm.FourException;

import java.util.function.Consumer;

public class STForeign extends STInstance {
    private final IMUDConnection connection;
    private final String id;

    public STForeign(IMUDConnection connection, String id) {
        this.connection = connection;
        this.id = id;
    }

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

    public IMUDConnection getConnection() { return this.connection; }
    public String getID() { return this.id; }

    public void send(STSymbol message, STInstance[] args, Consumer<STInstance> result) {
        this.connection.writeSend(result, this, message, args);
    }

    public void sendSuper(STSymbol message, STInstance[] args, Consumer<STInstance> result) {
        this.connection.writeSuperSend(result, this, message, args);
    }
}
