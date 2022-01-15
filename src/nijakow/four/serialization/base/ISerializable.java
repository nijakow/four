package nijakow.four.serialization.base;

public interface ISerializable {
    String getSerializationClassID();
    int getSerializationClassRevision();
    void serialize(ISerializer serializer);
}
