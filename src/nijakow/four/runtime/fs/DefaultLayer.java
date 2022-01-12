package nijakow.four.runtime.fs;

public class DefaultLayer extends Layer {
    public DefaultLayer(Filesystem fs) {
        super(fs);
    }

    @Override
    public ImmutableLayer createImmutable() {
        ImmutableLayer immutableLayer = new ImmutableLayer(getFilesystem());
        return (ImmutableLayer) copyTo(immutableLayer);
    }

    @Override
    public Layer createMutable() { return this; }
}
