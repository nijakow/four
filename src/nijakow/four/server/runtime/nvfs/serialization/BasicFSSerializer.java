package nijakow.four.server.runtime.nvfs.serialization;

import nijakow.four.server.runtime.nvfs.NVFileSystem;
import nijakow.four.server.runtime.nvfs.files.File;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;

public class BasicFSSerializer implements IFSSerializer {
    private final PrintStream stream;
    private final Queue<File> pendingFiles = new LinkedList<>();

    public BasicFSSerializer(OutputStream stream) {
        this.stream = new PrintStream(stream);
        this.stream.println("Version: 1");
        this.stream.println("Timestamp: " + Instant.now().toString());
    }

    @Override
    public void newMetaEntry(String type, byte[] payload) {
        stream.println();
        stream.println("+++ " + type);
        writeBase64Encoded(payload);
    }

    @Override
    public void newEntry(String id) {
        stream.println();
        stream.println("--- " + id);
    }

    @Override
    public void writeOwner(String name) {
        stream.println("Owner: " + new String(Base64.getEncoder().encode(name.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
    }

    @Override
    public void writeGroup(String name) {
        stream.println("Group: " + new String(Base64.getEncoder().encode(name.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
    }

    @Override
    public void writePermissions(int permissions) {
        stream.println("Permissions: 0" + Integer.toOctalString(permissions));
    }

    @Override
    public void writeBase64Encoded(byte[] bytes) {
        String encoded = new String(Base64.getEncoder().encode(bytes));
        stream.print('\t');
        for (int c = 0; c < encoded.length(); c++) {
            stream.print(encoded.charAt(c));
            if (c % 72 == 71) {
                stream.println();
                stream.print('\t');
            }
        }
        stream.println();
    }

    @Override
    public void queue(File file) {
        if (file != null)
            pendingFiles.add(file);
    }

    private final void loop() {
        while (!pendingFiles.isEmpty())
            pendingFiles.poll().writeOut(this);
    }

    private void serialize(File file) {
        queue(file);
    }

    public void serialize(NVFileSystem fs) {
        fs.writeOut(this);
        loop();
    }
}
