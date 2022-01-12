package nijakow.four.runtime.fs;

public class ImmutableLayer extends Layer {
    public ImmutableLayer(Filesystem fs) {
        super(fs);
    }

    @Override
    public ImmutableLayer createImmutable() {
        return this;
    }

    @Override
    public Layer createMutable() {
        DefaultLayer mutableLayer = new DefaultLayer(getFilesystem());
        return copyTo(mutableLayer);
    }

    @Override
    public Directory mkdir(String name) throws ImmutableException {
        throw new ImmutableException("Must not create directories inside of immutable layer!");
    }

    @Override
    public File touch(String name) throws ImmutableException {
        throw new ImmutableException("Must not create files inside of immutable layer!");
    }
}
