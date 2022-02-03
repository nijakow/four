package nijakow.four.server.serialization.fs.deserializer;

public abstract class FileEntry {
    private final String id;
    private final String user;
    private final String group;
    private final int permissions;
    private final String type;
    private final byte[] contents;

    protected FileEntry(String id, String user, String group, int permissions, String type, byte[] contents) {
        this.id = id;
        this.user = user;
        this.group = group;
        this.permissions = permissions;
        this.type = type;
        this.contents = contents;
    }
}
