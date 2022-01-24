package nijakow.four.server.serialization.base;

public interface ISerializable {
    String getSerializationClassID();
    void serialize(ISerializer serializer);
}
