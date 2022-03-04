package nijakow.four.server.serialization.fs.deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FileEntry {
    private final String id;
    private final StringBuilder payload;
    private String user;
    private String group;
    private int permissions;
    private boolean essential;
    private String type;
    private byte[] contents;

    protected FileEntry(String id) {
        this.id = id;
        this.user = "root";
        this.group = "users";
        this.permissions = 0644;
        this.essential = false;
        this.type = "data-file";
        this.payload = new StringBuilder();
    }

    public void setOwner(String user) { this.user = user; }
    public void setGroup(String group) { this.group = group; }
    public void setPermissions(int permissions) { this.permissions = permissions; }
    public void setEssential(boolean value) { this.essential = value; }
    public void setType(String type) { this.type = type; }
    public void appendPayload(String payload) { this.payload.append(payload); }

    public boolean isDirectory() {
        return "directory".equals(this.type);
    }

    public int getPermissions() { return permissions; }
    public boolean isEssential() { return essential; }
    public String getOwner() { return user; }
    public String getGroup() { return group; }

    public byte[] getPayloadAsBytes() { return Base64.getDecoder().decode(payload.toString()); }
    public String getPayloadAsString() { return new String(getPayloadAsBytes(), StandardCharsets.UTF_8); }
}
