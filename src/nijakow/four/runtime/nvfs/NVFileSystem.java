package nijakow.four.runtime.nvfs;

public class NVFileSystem implements FileParent {
    private Directory root;

    public NVFileSystem() {
        new Directory(this);
    }

    @Override
    public FileParent replaceThis(File me, File newMe) {
        System.out.println("NVFileSystem changed: " + newMe);
        root = newMe.asDirectory();
        return this;
    }

    @Override
    public File asFile() {
        return root;
    }

    @Override
    public String getMyName(File me) {
        return "";
    }

    public Directory getRoot() {
        return root;
    }

    public File lookup(String file) {
        return getRoot().lookup(file);
    }

    public TextFile touch(String name) {
        return root.touch(name);
    }

    public Directory mkdir(String name) {
        return root.mkdir(name);
    }
}
