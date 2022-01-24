package nijakow.four.serialization.textserializer;

import nijakow.four.serialization.base.ISerializable;
import nijakow.four.serialization.base.ISerializer;

public class Serializer implements ISerializer {
    private final Serializer parent;
    private final BranchingStringBuilder builder;

    public Serializer() {
        this(null, new BranchingStringBuilder());
    }

    private Serializer(Serializer parent, BranchingStringBuilder sb) {
        this.parent = parent;
        this.builder = sb;
        builder.append('E');
    }

    @Override
    public ISerializer close() {
        return parent;
    }

    @Override
    public ISerializer openEntry() {
        return new Serializer(this, builder.branch());
    }

    @Override
    public ISerializer openProperty(String name) {
        builder.append("P");
        writeString(name);
        return openEntry();
    }

    @Override
    public ISerializer writeObject(ISerializable serializable) {
        builder.append("O");
        writeString(serializable.getSerializationClassID());
        serializable.serialize(new Serializer(this, builder.branch()));
        return this;
    }

    @Override
    public ISerializer writeInt(int value) {
        builder.append('I');
        builder.append(value);
        return this;
    }

    @Override
    public ISerializer writeString(String value) {
        builder.append('S');
        builder.append(value.length());
        builder.append(':');
        return null;
    }
}
