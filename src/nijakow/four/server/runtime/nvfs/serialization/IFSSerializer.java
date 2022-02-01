package nijakow.four.server.runtime.nvfs.serialization;

import nijakow.four.server.runtime.nvfs.files.File;

public interface IFSSerializer {
    void newMetaEntry(String type, byte[] payload);
    void newEntry(String id);
    void writeOwner(String name);
    void writeGroup(String name);
    void writePermissions(int permissions);
    void writeBase64Encoded(byte[] bytes);
    void queue(File file);
}
