package nijakow.four.c.runtime.fs;

public abstract class Layer extends Directory {
    protected Layer(Filesystem fs) {
        super(fs, null, "");
    }

    public abstract ImmutableLayer createImmutable();
}