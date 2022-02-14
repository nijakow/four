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
    private boolean parsed = false;
    private String firstID = null;
    private Map<String, String> specials = new HashMap<>();

    public BasicFSDeserializer(InputStream input) {
        scanner = new Scanner(input);
    }

    public void constructOn(NVFileSystem nvfs, IdentityDatabase db) {
        ensureParsed();
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

    private void extractFileByID(NVFileSystem nvfs, Directory parent, String name, String id, IdentityDatabase db) {
        final FileEntry entry = getEntry(id);
        if (entry == null)
            return;
        boolean isDir = entry.isDirectory();
        int permissions = entry.getPermissions();
        User owner = db.getIdentityByName(entry.getOwner()).asUser();
        Group group = db.getIdentityByName(entry.getGroup()).asGroup();
        if (isDir) {
            Directory child = null;
            if (parent == null) {
                child = nvfs.getRoot();
                child.getRights().getUserAccessRights().setIdentity(owner);
                child.getRights().getGroupAccessRights().setIdentity(group);
            } else {
                child = parent.mkdir(name, null, owner, group);
            }
            if (child != null) {
                child.setmod(permissions);
                String text = entry.getPayloadAsString();
                for (final String line : text.split("\n")) {
                    final String[] toks = line.split(":");
                    extractFileByID(nvfs, child, toks[0], toks[1], db);
                }
            }
        } else {
            TextFile file = parent.touch(name, null, owner, group);
            if (file != null) {
                file.setmod(permissions);
                file.setContents(entry.getPayloadAsBytes());
            }
        }
    }

    public void restore(NVFileSystem nvfs, IdentityDatabase db) {
        db.restore(specials.getOrDefault("users", ""));
        extractFileByID(nvfs, null, "", firstID, db);
    }

    public void ensureParsed() {
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
                    payload.append(xline);
                }
                // TODO: Process the special declarations
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
                entry.appendPayload(line);
            }
        }
        parsed = true;
    }
}
