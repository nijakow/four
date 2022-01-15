package nijakow.four.serialization.base;

public interface ISerializable {
    String getSerializationClassID();
    void serialize(ISerializer serializer);
}
