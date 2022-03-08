package nijakow.four.server.storage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StorageManager {
    private final File directory;

    public StorageManager(File directory) {
        this.directory = directory;
        setup();
    }
    public StorageManager(String path) {
        this(new File(path));
    }

    private File getResource(String subpath) {
        return new File(directory.getPath() + subpath);
    }

    public OutputStream openResource(String subpath) {
        File file = getResource(subpath);
        if (file == null)
            return null;
        try {
            file.createNewFile();
            return new FileOutputStream(file);
        } catch (IOException e) {
            return null;
        }
    }

    public OutputStream startNewSnapshot() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        final String name = formatter.format(LocalDateTime.now()) + ".dat";
        return openResource("/snapshots/" + name);
    }

    public void setup() {
        directory.mkdirs();
        getResource("/snapshots").mkdir();
    }
}
