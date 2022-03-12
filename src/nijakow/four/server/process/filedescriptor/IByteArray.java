package nijakow.four.server.process.filedescriptor;

public interface IByteArray {
    byte get(int index);
    void put(int index, byte value);
    int getLength();
}
