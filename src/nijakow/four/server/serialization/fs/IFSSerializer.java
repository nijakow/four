package nijakow.four.server.serialization.fs;

import nijakow.four.server.nvfs.files.File;

public interface IFSSerializer {
    void newMetaEntry(String type, byte[] payload);
    void newEntry(String id);
    void writePath(String path);
    void writeOwner(String name);
    void writeGroup(String name);
    void writePermissions(int permissions);
    void writeIsPartOfStdlib(boolean flag);
    void writeIsEssential(boolean flag);
    void writeType(String type);
    void writeBase64Encoded(byte[] bytes);
    void queue(File file);
}
