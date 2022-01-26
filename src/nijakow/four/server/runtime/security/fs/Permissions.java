package nijakow.four.server.runtime.security.fs;

public class Permissions {
    private boolean readable;
    private boolean writable;
    private boolean executable;

    public Permissions(boolean readable, boolean writable, boolean executable) {
        this.readable = readable;
        this.writable = writable;
        this.executable = executable;
    }

    public boolean isReadable() {
        return readable;
    }

    public boolean isWritable() {
        return writable;
    }

    public boolean isExecutable() {
        return executable;
    }

    public void setReadable(boolean value) { readable = value; }
    public void setWritable(boolean value) { writable = value; }
    public void setExecutable(boolean value) { executable = value; }

    public void allowReadable() { setReadable(true); }
    public void allowWritable() { setWritable(true); }
    public void allowExecutable() { setExecutable(true); }

    public void disallowReadable() { setReadable(false); }
    public void disallowWritable() { setWritable(false); }
    public void disallowExecutable() { setExecutable(false); }
}
