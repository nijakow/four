package nijakow.four.runtime.nvfs;

public abstract class File<T extends SharedFileState> {
    private final FileParent parent;
    private final T state;

    protected File(FileParent parent, File previousThis, T state) {
        this.parent = parent.replaceThis(previousThis, this);
        this.state = state;
    }

    protected File(FileParent parent, File previousThis) {
        this.parent = parent.replaceThis(previousThis, this);
        this.state = createFileState();
    }

    protected File(FileParent parent, T state) {
        this.parent = parent;
        this.state = state;
    }

    protected FileParent getParent() { return parent; }
    protected T getState() { return state; }

    protected abstract T createFileState();
}
