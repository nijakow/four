package nijakow.four.runtime.objects;

import nijakow.four.runtime.exceptions.FourRuntimeException;
import nijakow.four.runtime.vm.Callback;
import nijakow.four.net.IConnection;

public class FConnection extends Instance {
	private final IConnection connection;
	
	@Override
	public FConnection asFConnection() { return this; }
	
	public void onReceive(Callback cb) {
		connection.onInput((s) -> {
			try {
				cb.invoke(new FString(s));
			} catch (FourRuntimeException e) {
				e.printStackTrace();  // TODO: Handle this gracefully
			}
		});
	}
	
	public void onDisconnect(Callback cb) {
		connection.onDisconnect(() -> {
			try {
				cb.invoke();
			} catch (FourRuntimeException e) {
				e.printStackTrace();  // TODO: Handle this gracefully
			}
		});
	}
	
	public void send(Instance instance) {
		connection.writeString(instance.asString());
	}
	
	public void close() {
		connection.close();
	}
	
	public FConnection(IConnection connection) {
		this.connection = connection;
	}
}
