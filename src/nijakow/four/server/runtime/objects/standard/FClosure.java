package nijakow.four.server.runtime.objects.standard;

import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.runtime.vm.fiber.Fiber;
import nijakow.four.server.storage.serialization.base.ISerializer;

public class FClosure extends Instance {
	private final Instance self;
	private final Instance instance;
	private final Key key;
	private final Instance[] args;

	@Override
	public String getType() { return "function"; }

	public FClosure(Blue self, Instance instance, Key key, Instance[] args) {
		this.self = self;
		this.instance = instance;
		this.key = key;
		this.args = args;
	}
	public FClosure(Blue self, Instance instance, Key key) {
		this(self, instance, key, new Instance[0]);
	}
	
	@Override
	public FClosure asFClosure() {
		return this;
	}

	@Override
	public void invoke(Fiber fiber, int args) throws FourRuntimeException {
		for (int x = 0; x < this.args.length; x++)
			fiber.push(this.args[x]);
		instance.extractMethod(fiber.getVM(), key).invoke(fiber, args + this.args.length, this.self);
	}

	public void invokeIn(Fiber fiber, int millis, int args) {
		fiber.getVM().invokeIn(fiber.getSharedState(), this, millis);
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
