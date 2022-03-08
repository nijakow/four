package nijakow.four.server.process.filedescriptor;

public interface IByteFile {
    byte getByte(int index);
    void putByte(int index, byte b);
    int getSize();
}
