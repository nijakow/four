package nijakow.four.server.runtime.nvfs;

import nijakow.four.server.runtime.objects.Blueprint;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.runtime.nvfs.files.Directory;
import nijakow.four.server.runtime.nvfs.files.File;
import nijakow.four.server.runtime.nvfs.files.TextFile;
import nijakow.four.server.runtime.objects.Blue;
import nijakow.four.server.serialization.base.ISerializable;
import nijakow.four.server.serialization.base.ISerializer;
import nijakow.four.share.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class NVFileSystem implements FileParent, ISerializable {
    private Directory root;

    public NVFileSystem() {
        this.root = new Directory(this);
    }

    @Override
    public File asFile() {
        return root;
    }

    @Override
    public String getMyName(File me) {
        return "";
    }

    @Override
    public String getMyFullName(File me) {
        return "";
    }

    public Directory getRoot() {
        return root;
    }

    public File resolve(String file) { return getRoot().resolve(file); }

    public TextFile resolveTextFile(String file) {
        File f = getRoot().resolve(file);
        return (f == null) ? null : f.asTextFile();
    }

    public Directory resolveDirectory(String file) {
        File f = getRoot().resolve(file);
        return (f == null) ? null : f.asDirectory();
    }

    private Pair<String, String> splitPath(String path) {
        int i = path.lastIndexOf('/');
        if (i < 0) {
            return new Pair("", path);
        } else {
            return new Pair(path.substring(0, i), path.substring(i + 1));
        }
    }

    public TextFile touch(String file) {
        Pair<String, String> path = splitPath(file);
        return resolve(path.getFirst()).asDirectory().touch(path.getSecond());
    }

    public Directory mkdir(String file) {
        Pair<String, String> path = splitPath(file);
        return resolve(path.getFirst()).asDirectory().mkdir(path.getSecond());
    }

    public boolean mv(String file, String loc) {
        File f = resolve(file);
        File target = resolve(loc);

        if (f != null) {
            if (target != null) {
                if (target.asDirectory() == null)
                    return false;
                f.moveTo(target.asDirectory(), f.getName());
            } else {
                Pair<String, String> path = splitPath(loc);
                target = resolve(path.getFirst());
                if (target == null || target.asDirectory() == null)
                    return false;
                f.moveTo(target.asDirectory(), path.getSecond());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean add(File file, String name) { return false; }

    @Override
    public boolean rename(File file, String name) { return false; }

    @Override
    public boolean remove(File file) { return getRoot().remove(file); }

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

    public void load(java.io.File file, String path) throws CompilationException, ParseException {
        final String name = file.getName();
        final String newPath = path + "/" + name;

        if (file.isDirectory()) {
            mkdir(newPath);
            for (java.io.File f : file.listFiles()) {
                load(f, newPath);
            }
        } else if (file.isFile()) {
            touch(newPath).setContents(getResourceText(file));
        }
    }

    public void load(java.io.File file) throws CompilationException, ParseException {
        for (java.io.File f : file.listFiles()) {
            load(f, "");
        }
    }

    public Blue getBlue(String name) throws CompilationException, ParseException {
        TextFile file = resolveTextFile(name);
        if (file == null)
            return null;
        file.compile();
        return file.getInstance();
    }

    public Blueprint getBlueprint(String name) {
        TextFile file = resolveTextFile(name);
        if (file == null)
            return null;
        return file.getBlueprint();
    }

    @Override
    public String getSerializationClassID() {
        return "NVFSFileSystem";
    }

    @Override
    public void serialize(ISerializer serializer) {
        getRoot().serialize(serializer);
    }
}
