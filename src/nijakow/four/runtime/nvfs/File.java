package nijakow.four.runtime.nvfs;

public abstract class File<T extends SharedFileState> {
    private FileParent parent;
    private T state;

    protected File(FileParent parent) {
        this.parent = parent;
        this.state = createFileState();
    }

    protected FileParent getParent() { return parent; }
    public File getRoot() { return getParent().getRoot(); }
    protected T getState() { return state; }

    protected abstract T createFileState();

    public TextFile asTextFile() { return null; }
    public Directory asDirectory() { return null; }

    void rm() { getParent().remove(this); }

    public String getName() {
        return getParent().getMyName(this);
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
