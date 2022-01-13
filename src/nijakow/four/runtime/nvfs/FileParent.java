package nijakow.four.runtime.nvfs;

public interface FileParent {
    File getRoot();
    File asFile();
    String getMyName(File me);
    void remove(File file);
}
