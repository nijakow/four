package nijakow.four.server.runtime.objects.misc;

import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.FloatingInstance;
import nijakow.four.server.runtime.objects.collections.FList;
import nijakow.four.server.runtime.objects.standard.FString;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.types.ListType;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.server.runtime.vm.Callback;
import nijakow.four.server.net.IConnection;

public class FConnection extends FloatingInstance {
	private final IConnection connection;

	@Override
	public String getType() { return "connection"; }

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

	public void onEscape(Callback cb) {
		connection.onEscape((strs) -> {
			try {
				FList lst = new FList(Type.getString().listType());
				for (String s : strs)
					lst.append(new FString(s));
				cb.invoke(lst);
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
		registerToPool();
	}
}
