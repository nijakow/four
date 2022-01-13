package nijakow.four.runtime.fs;

public abstract class Layer extends Directory {
    protected Layer(Filesystem fs) {
        super(fs, null, "");
    }

    public abstract ImmutableLayer createImmutable();

    public abstract Layer createMutable();

    protected FSNode reconstructStructureFor(FSNode node) {
        if (node.getParent() == node.getRoot()) {
           /* if (node.asFile() != null) {
                File copy = new File(getFilesystem(), this, node.getName());
                copy.setContents(node.asFile().getContents());
                this.insertNode(copy);
                return copy;
            } else {*/
                Directory copy = new Directory(getFilesystem(), this, node.getName());
                this.insertNode(copy);
                return copy;
            //}
        } else {
            //if (node.asDir() != null) {
                FSNode copy = new Directory(getFilesystem(), reconstructor(node), node.getName());
                insertNode(copy);
                return copy;
            //}
        }
    }

    private FSNode reconstructor(FSNode oldNode) {
        if (oldNode.getParent() == oldNode.getRoot()) {
            return this;
        }
        FSNode np = reconstructor(oldNode.getParent());
        FSNode copy = new Directory(getFilesystem(), np, oldNode.getName());
        np.asDir().insertNode(copy);
        return copy;
    }

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
