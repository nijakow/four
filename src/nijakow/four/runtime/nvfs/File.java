package nijakow.four.runtime.nvfs;

public abstract class File<T extends SharedFileState> {
    private final FileParent parent;
    private final T state;

    protected File(FileParent parent, File previousThis, T state) {
        this.parent = parent.replaceThis(previousThis, this);
        this.state = (state == null) ? createFileState() : state;
    }

    protected File(FileParent parent, File previousThis) {
        this(parent, previousThis, null);
    }

    protected File(FileParent parent, T state) {
        this.parent = parent;
        this.state = state;
    }

    protected FileParent getParent() { return parent; }
    public File getRoot() { return getParent().getRoot(); }
    protected T getState() { return state; }

    protected abstract T createFileState();

    public TextFile asTextFile() { return null; }
    public Directory asDirectory() { return null; }

    void rm() { getParent().replaceThis(this, null); }

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
        if (path.startsWith("/")) return getRoot().resolve(path.substring(1));

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
