package nijakow.four.serialization.base;

public interface ISerializer extends IBasicSerializer {
    ISerializer openProperty(String name);
    IArraySerializer openArray();
    ISerializer writeObject(ISerializable serializable);
    ISerializer writeInt(int value);
    ISerializer writeString(String value);
}
