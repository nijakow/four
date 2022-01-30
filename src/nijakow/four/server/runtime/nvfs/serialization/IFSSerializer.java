package nijakow.four.server.runtime.nvfs.serialization;

import nijakow.four.server.runtime.nvfs.files.File;

public interface IFSSerializer {
    void newEntry(String path); // Convention: Directories end with "/"
    void writeOwner(String id, String name);
    void writeGroup(String id, String name);
    void writePermissions(int permissions);
    void writeBase64Encoded(String text);
    void queue(File file);
}
