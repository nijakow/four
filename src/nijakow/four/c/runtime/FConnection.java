package nijakow.four.c.runtime;

import nijakow.four.net.IConnection;

public class FConnection extends Instance {
	private final IConnection connection;
	
	public void sendInt(Instance instance) {
		connection.writeString(Integer.toString(instance.asInt()));
	}
	
	public void sendString(Instance instance) {
		connection.writeString(instance.asString());
	}
	
	public FConnection(IConnection connection) {
		this.connection = connection;
	}
}
