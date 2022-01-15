package nijakow.four.serialization.base;

public interface ISerializer {
    ISerializer openField();
    IArraySerializer openArray();
    IMappingSerializer openMapping();
    void writeObject(ISerializable serializable);
    void writeInt(int value);
    void writeString(String value);
}
