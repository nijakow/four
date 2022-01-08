package nijakow.four.c.runtime.fs;

public class ImmutableLayer extends Layer {
    public ImmutableLayer(Filesystem fs) {
        super(fs);
    }

    @Override
    public ImmutableLayer createImmutable() {
        return this;
    }

    public Layer createMutable() {
        DefaultLayer mutableLayer = new DefaultLayer(getFilesystem());
        for (FSNode node : getChildren()) {
            mutableLayer.insertNode(copyOf(node, mutableLayer));
        }
        return mutableLayer;
    }

    private FSNode copyOf(FSNode node, FSNode newParent) {
        FSNode copy;
        if (node instanceof Directory) {
            copy = new Directory(getFilesystem(), newParent, node.getName());
            for (FSNode n : ((Directory) node).getChildren()) {
                copy.asDir().insertNode(copyOf(n, copy));
            }
        } else {
            copy = new File(getFilesystem(), newParent, node.getName());
            copy.asFile().setContents(node.asFile().getContents());
        }
        return copy;
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
