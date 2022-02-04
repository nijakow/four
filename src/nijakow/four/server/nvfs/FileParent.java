package nijakow.four.server.nvfs;

import nijakow.four.server.nvfs.files.File;
import nijakow.four.server.runtime.security.users.Identity;

public interface FileParent {
    File getRoot();
    File asFile();
    String getMyName(File me);
    String getMyFullName(File me);
    boolean add(File file, String name);
    boolean rename(File file, String name);
    boolean remove(File file);

    boolean hasWriteAccess(Identity identity);
}
