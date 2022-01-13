package nijakow.four.runtime.nvfs;

import nijakow.four.runtime.fs.Filesystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class NVFileSystem implements FileParent {
    private Directory root;

    public NVFileSystem() {
        new Directory(this);
    }

    @Override
    public FileParent replaceThis(File me, File newMe) {
        System.out.println("NVFileSystem changed: " + newMe);
        root = newMe.asDirectory();
        return this;
    }

    @Override
    public File asFile() {
        return root;
    }

    @Override
    public String getMyName(File me) {
        return "";
    }

    public Directory getRoot() {
        return root;
    }

    public File resolve(String file) {
        return getRoot().resolve(file);
    }

    public TextFile touch(String name) {
        return getRoot().touch(name);
    }

    public Directory mkdir(String name) {
        return getRoot().mkdir(name);
    }

    private String getResourceText(java.io.File file) {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file))) {
            StringBuilder builder = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
            return builder.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public void load(java.io.File file, String path) {
        final String name = file.getName();
        final String newPath = path + "/" + name;

        if (file.isDirectory()) {
            resolve(path).asDirectory().mkdir(name);
            for (java.io.File f : file.listFiles()) {
                load(f, newPath);
            }
        } else if (file.isFile()) {
            touch(newPath).setContents(getResourceText(file));
        }
    }

    public void load(java.io.File file) {
        load(file, "");
    }
}
