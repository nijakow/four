package nijakow.four.runtime.nvfs;

import java.util.HashMap;
import java.util.Map;

public class Directory extends File<SharedDirectoryState> implements FileParent {
    private final Map<String, File> files;

    public Directory(FileParent parent) {
        this(parent, new HashMap<>());
    }

    public Directory(FileParent parent, Map<String, File> children) {
        super(parent, (File) null);
        files = children;
    }

    private Directory(FileParent parent, Directory previousThis, Map<String, File> children) {
        super(parent, previousThis, previousThis.getState());
        this.files = children;
    }

    @Override
    protected SharedDirectoryState createFileState() {
        return new SharedDirectoryState();
    }

    @Override
    public Directory asDirectory() {
        return this;
    }

    @Override
    public FileParent replaceThis(File me, File newMe) {
        if (me == null) {
            // We already have a null slot in here!
            for (String name : files.keySet()) {
                if (files.get(name) == null)
                    files.put(name, newMe);
            }
            return this;
        } else {
            Map<String, File> copy = new HashMap<>();
            for (String name : files.keySet()) {
                final File f = copy.get(name);
                copy.put(name, ((f == me) || (f == null)) ? newMe : f);
            }
            return new Directory(getParent(), this, copy);
        }
    }

    private Directory duplicateWithEntry(String name) {
        Map<String, File> copy = new HashMap<>();
        for (String key : files.keySet())
            copy.put(key, files.get(key));
        copy.put(name, null);
        return new Directory(getParent(), copy);
    }

    public TextFile touch(String name) {
        return new TextFile(duplicateWithEntry(name), "");
    }
}
