package nijakow.four.server.serialization.fs.deserializer;

import nijakow.four.server.nvfs.NVFileSystem;
import nijakow.four.server.runtime.security.users.IdentityDatabase;

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

    public void ensureParsed() {
        FileEntry entry = null;

        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            if (line.trim().isEmpty());
            else if (line.startsWith("--- ")) {
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
