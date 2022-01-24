package nijakow.four.runtime.nvfs.files;

import nijakow.four.runtime.nvfs.FileParent;
import nijakow.four.runtime.nvfs.shared.SharedDirectoryState;
import nijakow.four.serialization.base.ISerializer;
import nijakow.four.util.Pair;

import java.util.*;

public class Directory extends File<SharedDirectoryState> implements FileParent {
    private final Map<String, File> files;

    public Directory(FileParent parent) {
        super(parent);
        this.files = new HashMap<>();
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
    public File asFile() {
        return this;
    }


    public TextFile touch(String name) {
        TextFile file = new TextFile(this);
        files.put(name, file);
        return file;
    }

    public Directory mkdir(String name) {
        Directory dir = new Directory(this);
        files.put(name, dir);
        return dir;
    }

    @Override
    public boolean add(File file, String name) {
        files.put(name, file);
        return true;
    }

    @Override
    public boolean rename(File file, String name) {
        remove(file);
        files.put(name, file);
        return true;
    }

    @Override
    public boolean remove(File file) {
        List<String> names = new LinkedList<>();
        for (String name : files.keySet()) {
            if (files.get(name) == file)
                names.add(name);
        }
        for (String name : names)
            files.remove(name);
        return true;
    }

    public Pair<String, File>[] ls() {
        List<Pair<String, File>> elements = new ArrayList<>();
        for (String key : files.keySet()) {
            elements.add(new Pair<>(key, files.get(key)));
        }
        return elements.toArray(new Pair[0]);
    }

    @Override
    public String getMyName(File me) {
        for (String key : files.keySet())
            if (files.get(key) == me)
                return key;
        return "???";
    }

    @Override
    public String getMyFullName(File me) {
        for (String key : files.keySet())
            if (files.get(key) == me)
                return getParent().getMyFullName(this) + "/" + key;
        return "???";
    }

    @Override
    public File resolve1(String name) {
        File f = files.get(name);
        if (f != null) return f;
        else return super.resolve1(name);
    }

    @Override
    public String getSerializationClassID() {
        return "NVFSDirectory";
    }

    @Override
    public void serialize(ISerializer serializer) {
        serializeCore(serializer);
        ISerializer mapser = serializer.openProperty("directory.files");
        for (String key : files.keySet()) {
            mapser.openProperty(key).writeObject(files.get(key)).close();
        }
        mapser.close();
    }
}
