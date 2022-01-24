package nijakow.four.server.runtime.nvfs.files;

import nijakow.four.server.runtime.nvfs.FileParent;
import nijakow.four.server.runtime.nvfs.shared.SharedFileState;
import nijakow.four.serialization.base.ISerializable;
import nijakow.four.serialization.base.ISerializer;

public abstract class File<T extends SharedFileState> implements ISerializable {
    private FileParent parent;
    private T state;

    protected File(FileParent parent) {
        this.parent = parent;
        this.state = createFileState();
    }

    protected void serializeCore(ISerializer serializer) {
        serializer.openProperty("file.name").writeString(getName()).close();
        serializer.openProperty("file.path").writeString(getFullName()).close();
    }

    protected FileParent getParent() { return parent; }
    public File getRoot() { return getParent().getRoot(); }
    protected T getState() { return state; }

    protected abstract T createFileState();

    public TextFile asTextFile() { return null; }
    public Directory asDirectory() { return null; }

    public void rm() { getParent().remove(this); }
    public void moveTo(FileParent parent, String name) {
        if (parent == null)
            rm();
        else if (parent == getParent())
            getParent().rename(this, name);
        else if (parent.add(this, name)) {
            rm();
            this.parent = parent;
        }
    }

    public String getName() {
        return getParent().getMyName(this);
    }

    public String getFullName() {
        return getParent().getMyFullName(this);
    }

    public File resolve1(String name) {
        if ("".equals(name)) return this;
        else if (".".equals(name)) return this;
        else if ("..".equals(name)) return getParent().asFile();
        else return null;
    }

    public File resolve(String path) {
        if (path.startsWith("/"))
            return getRoot().resolve(path.substring(1));

        File current = this;
        String[] tokens = path.split("/");

        for (String token : tokens) {
            if (current == null)
                break;
            current = current.resolve1(token);
        }

        return current;
    }
}
