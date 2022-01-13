package nijakow.four.runtime.nvfs;

public interface FileParent {
    File getRoot();
    File asFile();
    FileParent replaceThis(File me, File newMe);
    String getMyName(File me);
}
