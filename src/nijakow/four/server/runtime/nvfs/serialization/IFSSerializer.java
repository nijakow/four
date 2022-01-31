package nijakow.four.server.runtime.nvfs.serialization;

import nijakow.four.server.runtime.nvfs.files.File;

public interface IFSSerializer {
    void newEntry(String id);
    void writeOwner(String id, String name);
    void writeGroup(String id, String name);
    void writePermissions(int permissions);
    void writeBase64Encoded(byte[] bytes);
    void queue(File file);
}
