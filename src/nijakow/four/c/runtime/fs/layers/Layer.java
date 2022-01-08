package nijakow.four.c.runtime.fs.layers;

import nijakow.four.c.runtime.fs.Directory;
import nijakow.four.c.runtime.fs.File;
import nijakow.four.c.runtime.fs.Filesystem;

public abstract class Layer extends Directory {
    protected Layer(Filesystem fs) {
        super(fs, null, "");
    }

    public abstract ImmutableLayer createImmutable();
}