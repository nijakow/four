package nijakow.four.c.runtime;

import nijakow.four.net.IConnection;

public class FConnection extends Instance {
	private final IConnection connection;
	
	@Override
	public FConnection asFConnection() { return this; }
	
	public void send(Instance instance) {
		connection.writeString(instance.asString());
	}
	
	public FConnection(IConnection connection) {
		this.connection = connection;
	}
}
