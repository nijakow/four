package nijakow.four.c.runtime.fs;

public class DefaultLayer extends Layer {
    public DefaultLayer(Filesystem fs) {
        super(fs);
    }

    @Override
    public ImmutableLayer createImmutable() {
        ImmutableLayer immutableLayer = new ImmutableLayer(getFilesystem());
        for (FSNode node : getChildren()) {
            immutableLayer.insertNode(copyOf(node, immutableLayer));
        }
        return immutableLayer;
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
}
