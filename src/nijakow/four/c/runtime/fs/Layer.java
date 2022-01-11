package nijakow.four.c.runtime.fs;

public abstract class Layer extends Directory {
    protected Layer(Filesystem fs) {
        super(fs, null, "");
    }

    public abstract ImmutableLayer createImmutable();

    public abstract Layer createMutable();

    protected Layer copyTo(Layer layer) {
        for (FSNode node : getChildren()) {
            layer.insertNode(copyOf(node, layer));
        }
        return layer;
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
