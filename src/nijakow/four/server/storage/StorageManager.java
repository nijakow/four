package nijakow.four.server.storage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class StorageManager {
    private final File directory;

    public StorageManager(File directory) {
        this.directory = directory;
        setup();
    }
    public StorageManager(String path) {
        this(new File(path));
    }

    public void setup() {
        directory.mkdirs();
        getResource("/snapshots").mkdir();
    }

    private File getResource(String subpath) {
        return new File(directory.getPath() + subpath);
    }

    public OutputStream createResource(String subpath) {
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

    public InputStream openResource(String subpath) {
        File file = getResource(subpath);
        if (file == null)
            return null;
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            return null;
        }
    }

    public OutputStream startNewSnapshot() {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        final String name = formatter.format(LocalDateTime.now()) + ".dat";
        return createResource("/snapshots/" + name);
    }

    private String[] getSnapshots() {
        final File snapshotDir = getResource("/snapshots");
        if (snapshotDir == null) return new String[0];
        final File[] files = snapshotDir.listFiles();
        if (files == null) return new String[0];
        final String[] names = new String[files.length];
        for (int x = 0; x < names.length; x++)
            names[x] = files[x].getName();
        Arrays.sort(names);
        return names;
    }

    public InputStream getLatestSnapshot() {
        String[] names = getSnapshots();
        if (names == null || names.length == 0)
            return null;
        return openResource("/snapshots/" + names[names.length - 1]);
    }
}
