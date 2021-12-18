package nijakow.four.c.runtime;

import nijakow.four.c.runtime.vm.Callback;
import nijakow.four.net.IConnection;

public class FConnection extends Instance {
	private final IConnection connection;
	
	@Override
	public FConnection asFConnection() { return this; }
	
	public void onReceive(Callback cb) {
		connection.onInput((s) -> cb.invoke(new FString(s)));
	}
	
	public void onDisconnect(Callback cb) {
		connection.onDisconnect(() -> cb.invoke());
	}
	
	public void send(Instance instance) {
		connection.writeString(instance.asString());
	}
	
	public FConnection(IConnection connection) {
		this.connection = connection;
	}
}
