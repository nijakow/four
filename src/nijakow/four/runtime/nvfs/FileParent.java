package nijakow.four.runtime.nvfs;

import nijakow.four.runtime.nvfs.files.File;

public interface FileParent {
    File getRoot();
    File asFile();
    String getMyName(File me);
    String getMyFullName(File me);
    boolean add(File file, String name);
    boolean rename(File file, String name);
    boolean remove(File file);
}
