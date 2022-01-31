package nijakow.four.server.runtime.nvfs.serialization;

import nijakow.four.server.runtime.nvfs.NVFileSystem;
import nijakow.four.server.runtime.nvfs.files.File;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
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
        this.stream.println("Datestamp: " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    }

    @Override
    public void newEntry(String path) {
        stream.println();
        stream.println("--- " + path);
    }

    @Override
    public void writeOwner(String id, String name) {
        stream.println("Owner: " + id + " " + name);
    }

    @Override
    public void writeGroup(String id, String name) {
        stream.println("Group: " + id + " " + name);
    }

    @Override
    public void writePermissions(int permissions) {
        stream.println("Permissions: 0" + Integer.toOctalString(permissions));
    }

    @Override
    public void writeBase64Encoded(String text) {
        String encoded = new String(Base64.getEncoder().encode(text.getBytes(StandardCharsets.UTF_8)));
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
