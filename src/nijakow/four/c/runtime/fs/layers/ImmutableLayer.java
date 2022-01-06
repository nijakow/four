package nijakow.four.c.runtime.fs.layers;

import nijakow.four.c.runtime.fs.Filesystem;

public class ImmutableLayer extends Layer {
    public ImmutableLayer(Filesystem fs) {
        super(fs);
    }

    @Override
    public ImmutableLayer createImmutable() {
        return this;
    }
}
