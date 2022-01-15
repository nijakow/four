package nijakow.four.runtime.nvfs;

public interface FileParent {
    File getRoot();
    File asFile();
    String getMyName(File me);
    String getMyFullName(File me);
    boolean add(String name, File file);
    void remove(File file);
}
