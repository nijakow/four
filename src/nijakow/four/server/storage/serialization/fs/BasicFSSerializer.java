package nijakow.four.server.storage.serialization.fs;

import nijakow.four.server.nvfs.NVFileSystem;
import nijakow.four.server.nvfs.files.File;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.Instant;
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
    public void writeName(String name) {
        stream.println("Name: " + name);
    }

    @Override
    public void writePath(String path) {
        stream.println("Path: " + path);
    }

    @Override
    public void writeOwner(String name) {
        stream.println("Owner: " + name);
    }

    @Override
    public void writeGroup(String name) {
        stream.println("Group: " + name);
    }

    @Override
    public void writePermissions(int permissions) {
        stream.println("Permissions: 0" + Integer.toOctalString(permissions));
    }

    @Override
    public void writeIsPartOfStdlib(boolean flag) {
        stream.println("StandardLib: " + (flag ? "yes" : "no"));
    }

    @Override
    public void writeIsEssential(boolean flag) {
        stream.println("Essential: " + (flag ? "yes" : "no"));
    }

    @Override
    public void writeType(String type) {
         stream.println("Type: " + type);
    }

    @Override
    public void writeBase64Encoded(byte[] bytes) {
        String encoded = new String(Base64.getEncoder().encode(bytes));
        stream.print('\t');
        for (int c = 0; c < encoded.length(); c++) {
            stream.print(encoded.charAt(c));
            if (c % 64 == 63) {
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

    public void serialize(File file) {
        queue(file);
        loop();
    }

    public void serialize(NVFileSystem fs) {
        fs.writeOut(this);
        loop();
    }
}
