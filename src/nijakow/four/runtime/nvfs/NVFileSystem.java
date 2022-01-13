package nijakow.four.runtime.nvfs;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.parser.ParseException;
import nijakow.four.runtime.Blue;
import nijakow.four.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class NVFileSystem implements FileParent {
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

    public Directory getRoot() {
        return root;
    }

    public File resolve(String file) {
        return getRoot().resolve(file);
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

    public void remove(File file) { getRoot().remove(file); }

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
        TextFile file = resolve(name).asTextFile();
        file.compile();
        return resolve(name).asTextFile().getInstance();
    }
}
