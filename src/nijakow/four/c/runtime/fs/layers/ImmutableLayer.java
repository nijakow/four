package nijakow.four.c.runtime.fs.layers;

import nijakow.four.c.runtime.fs.Directory;
import nijakow.four.c.runtime.fs.File;
import nijakow.four.c.runtime.fs.Filesystem;

public class ImmutableLayer extends Layer {
    public ImmutableLayer(Filesystem fs) {
        super(fs);
    }

    @Override
    public ImmutableLayer createImmutable() {
        return this;
    }

    @Override
    public Directory mkdir(String name) {
        throw new RuntimeException("Must not create directories inside of immutable layer!");
    }

    @Override
    public File touch(String name) {
        throw new RuntimeException("Must not create files inside of immutable layer!");
    }
}
