package nijakow.four.server.serialization.fs.deserializer;

import nijakow.four.server.nvfs.NVFileSystem;
import nijakow.four.server.nvfs.files.Directory;
import nijakow.four.server.nvfs.files.File;
import nijakow.four.server.nvfs.files.TextFile;
import nijakow.four.server.runtime.security.users.Group;
import nijakow.four.server.runtime.security.users.IdentityDatabase;
import nijakow.four.server.runtime.security.users.User;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BasicFSDeserializer {
    private final Scanner scanner;
    private final Map<String, FileEntry> files = new HashMap<>();
    private String firstID = null;
    private Map<String, String> specials = new HashMap<>();
    private boolean parsed = false;

    public BasicFSDeserializer(InputStream input) {
        scanner = new Scanner(input);
    }

    private byte[] decodeBase64AsBytes(String b64) {
        return Base64.getDecoder().decode(b64);
    }

    private String decodeBase64AsString(String b64) {
        return new String(decodeBase64AsBytes(b64), StandardCharsets.UTF_8);
    }

    private FileEntry getEntry(String id) {
        return files.get(id);
    }

    private boolean extractFileByID(NVFileSystem nvfs, Directory parent, String name, String id, IdentityDatabase db) {
        if (parent != null)
            System.out.println("Restoring " + id + " (" + parent.getFullName() + "/" + name + ")");
        else
            System.out.println("Restoring " + id);
        final FileEntry entry = getEntry(id);
        if (entry == null)
            return false;
        boolean result = true;
        boolean isDir = entry.isDirectory();
        int permissions = entry.getPermissions();
        User owner = db.getIdentityByName(entry.getOwner()).asUser();
        Group group = db.getIdentityByName(entry.getGroup()).asGroup();
        if (isDir) {
            Directory child;
            if (parent == null) {
                child = nvfs.getRoot();
                child.getRights().getUserAccessRights().setIdentity(owner);
                child.getRights().getGroupAccessRights().setIdentity(group);
            } else {
                child = parent.mkdir(name, db.getRootUser(), owner, group);
            }
            if (child == null)
                return false;
            child.setmod(permissions);
            extractDirectoryChildren(nvfs, child, entry, db);
        } else {
            TextFile file = parent.touch(name, db.getRootUser(), owner, group);
            if (file == null)
                return false;
            file.setmod(permissions);
            file.setContents(entry.getPayloadAsBytes());
        }
        return result;
    }

    private boolean extractDirectoryChildren(NVFileSystem nvfs, Directory parent, FileEntry entry, IdentityDatabase db) {
        boolean result = true;
        String text = entry.getPayloadAsString();
        for (final String line : text.split("\n")) {
            final String[] toks = line.split(":");
            if (!extractFileByID(nvfs, parent, toks[1], toks[0], db))
                result = false;
        }
        return result;
    }

    public void restore(NVFileSystem nvfs, IdentityDatabase db) {
        restore(nvfs, db, null, "");
    }

    public void restore(NVFileSystem nvfs, IdentityDatabase db, Directory parent) {
        ensureParsed();
        db.restore(specials.getOrDefault("users", ""));
        extractDirectoryChildren(nvfs, parent, getEntry(firstID), db);
    }

    public void restore(NVFileSystem nvfs, IdentityDatabase db, Directory parent, String name) {
        ensureParsed();
        db.restore(specials.getOrDefault("users", ""));
        extractFileByID(nvfs, parent, name, firstID, db);
    }

    public void ensureParsed() {
        if (parsed) return;

        FileEntry entry = null;

        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            if (line.trim().isEmpty());
            else if (line.startsWith("+++ ")) {
                final String type = line.substring(4);
                final StringBuilder payload = new StringBuilder();
                while (scanner.hasNextLine()) {
                    final String xline = scanner.nextLine();
                    if (!xline.startsWith("\t"))
                        break;
                    payload.append(xline.substring(1));
                }
                specials.put(type, decodeBase64AsString(payload.toString()));
            } else if (line.startsWith("--- ")) {
                final String id = line.substring(4);
                if (firstID == null) firstID = id;
                entry = new FileEntry(id);
                files.put(id, entry);
            } else if (line.startsWith("Owner: ")) {
                entry.setOwner(decodeBase64AsString(line.substring(7)));
            } else if (line.startsWith("Group: ")) {
                entry.setGroup(decodeBase64AsString(line.substring(7)));
            } else if (line.startsWith("Permissions: ")) {
                entry.setPermissions(Integer.parseInt(line.substring(13), 8));
            } else if (line.startsWith("Type: ")) {
                entry.setType(line.substring(6));
            } else if (line.startsWith("\t")) {
                entry.appendPayload(line.substring(1));
            } else {
                System.err.println("WARNING: Unknown line from dump file: " + line);
            }
        }
        parsed = true;
    }
}
