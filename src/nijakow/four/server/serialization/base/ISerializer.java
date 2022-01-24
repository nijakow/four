package nijakow.four.server.serialization.base;

public interface ISerializer extends IBasicSerializer {
    ISerializer openEntry();
    ISerializer openProperty(String name);
    ISerializer writeObject(ISerializable serializable);
    ISerializer writeInt(int value);
    ISerializer writeString(String value);
}
