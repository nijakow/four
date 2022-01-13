package nijakow.four.runtime.nvfs;

public interface FileParent {
    File asFile();
    FileParent replaceThis(File me, File newMe);
}
