package nijakow.four.serialization.base;

public interface ISerializer extends IBasicSerializer {
    ISerializer openProperty(String name);
    IArraySerializer openArray();
    IMappingSerializer openMapping();
    ISerializer writeObject(ISerializable serializable);
    ISerializer writeInt(int value);
    ISerializer writeString(String value);
}
