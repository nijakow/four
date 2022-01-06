package nijakow.four.c.runtime.fs.layers;

import nijakow.four.c.runtime.fs.Filesystem;

public class DefaultLayer extends Layer {
    public DefaultLayer(Filesystem fs) {
        super(fs);
    }

    @Override
    public ImmutableLayer createImmutable() {
        // TODO Convert to immutable
        return null;
    }
}
