package nijakow.four.server.runtime.objects;

import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.vm.Fiber;
import nijakow.four.server.serialization.base.ISerializer;

public class FClosure extends Instance {
	private final Instance self;
	private final Instance instance;
	private final Key key;
	
	public FClosure(Blue self, Instance instance, Key key) {
		this.self = self;
		this.instance = instance;
		this.key = key;
	}
	
	@Override
	public FClosure asFClosure() {
		return this;
	}

	@Override
	public void invoke(Fiber fiber, int args) throws FourRuntimeException {
		instance.extractMethod(fiber.getVM(), key).invoke(fiber, args, this.self);
	}

	@Override
	public String getSerializationClassID() {
		return "closure";
	}

	@Override
	public void serialize(ISerializer serializer) {
		serializer.openProperty("instance.self").writeObject(self).close();
		serializer.openProperty("instance.instance").writeObject(instance).close();
		serializer.openProperty("instance.selector").writeString(key.getName()).close();
	}
}