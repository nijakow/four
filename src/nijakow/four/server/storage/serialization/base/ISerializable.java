package nijakow.four.server.storage.serialization.base;

public interface ISerializable {
    String getSerializationClassID();
    void serialize(ISerializer serializer);
}
