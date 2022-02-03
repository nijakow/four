package nijakow.four.server.serialization.fs.deserializer;

public class FileEntry {
    private final String id;
    private final StringBuilder payload;
    private String user;
    private String group;
    private int permissions;
    private String type;
    private byte[] contents;

    protected FileEntry(String id) {
        this.id = id;
        this.user = "root";
        this.group = "users";
        this.permissions = 0644;
        this.type = "data-file";
        this.payload = new StringBuilder();
    }

    public void setOwner(String user) { this.user = user; }
    public void setGroup(String group) { this.group = group; }
    public void setPermissions(int permissions) { this.permissions = permissions; }
    public void setType(String type) { this.type = type; }
    public void appendPayload(String payload) { this.payload.append(payload); }
}
