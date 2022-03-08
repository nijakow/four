package nijakow.four.server.process.filedescriptor;

public interface IFileDescriptor {
    int read(IByteArray buffer);
    int write(IByteArray buffer);
    boolean seekFromZero(int offset);
    boolean seekFromCurrent(int offset);
    boolean seekFromEnd(int offset);
    int tell();
    void close();
}
