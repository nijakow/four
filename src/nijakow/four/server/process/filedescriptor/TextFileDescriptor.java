package nijakow.four.server.process.filedescriptor;

public class TextFileDescriptor implements IFileDescriptor {
    private final IByteFile file;
    private int index;
    private boolean closed = false;

    public TextFileDescriptor(IByteFile file) {
        this.file = file;
        this.index = 0;
    }

    @Override
    public int read(IByteArray buffer) {
        if (closed) return -1;
        int i = 0;
        while (i < buffer.getLength() && index < file.getSize()) {
            buffer.put(i++, file.getByte(index++));
        }
        return i;
    }

    @Override
    public int write(IByteArray buffer) {
        if (closed) return -1;
        int i = 0;
        while (i < buffer.getLength() && index < file.getSize()) {
            file.putByte(index++, buffer.get(i++));
        }
        return i;
    }

    @Override
    public boolean seekFromZero(int offset) {
        if (closed) return false;
        if (offset >= 0 && offset < file.getSize()) {
            index = offset;
            return true;
        }
        return false;
    }

    @Override
    public boolean seekFromCurrent(int offset) {
        return seekFromZero(index + offset);
    }

    @Override
    public boolean seekFromEnd(int offset) {
        return seekFromZero(file.getSize() - offset);
    }

    @Override
    public int tell() {
        if (closed) return -1;
        return index;
    }

    @Override
    public void close() {
        closed = true;
    }
}
