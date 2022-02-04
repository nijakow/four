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

    private void extractFileByID(Directory parent, String name, String id, IdentityDatabase db) {
        final FileEntry entry = getEntry(id);
        if (entry == null)
            return;
        boolean isDir = entry.isDirectory();
        User owner = db.getIdentityByName(entry.getOwner()).asUser();
        Group group = db.getIdentityByName(entry.getGroup()).asGroup();
        if (isDir) {
            parent.mkdir(name, null, owner, group);
        } else {
            parent.touch(name, null, owner, group);
        }
    }

    private void restore(NVFileSystem nvfs) {

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
