package nijakow.four.server.nvfs.files;

import nijakow.four.server.nvfs.FileParent;
import nijakow.four.server.storage.serialization.fs.IFSSerializer;
import nijakow.four.server.users.Group;
import nijakow.four.server.users.Identity;
import nijakow.four.server.users.User;
import nijakow.four.server.storage.serialization.base.ISerializer;
import nijakow.four.share.util.Pair;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Directory extends File implements FileParent {
    private final Map<String, File> files;

    public Directory(FileParent parent, User owner, Group gowner) {
        super(parent, owner, gowner);
        this.files = new HashMap<>();
    }

    @Override
    public Directory asDirectory() {
        return this;
    }

    @Override
    public File asFile() {
        return this;
    }

    @Override
    public boolean isEssential() {
        for (File f : files.values())
            if (f.isEssential())
                return true;
        return super.isEssential();
    }

    @Override
    public boolean shouldBeSerialized() {
        for (File f : files.values())
            if (f.shouldBeSerialized())
                return true;
        return super.shouldBeSerialized();
    }

    @Override
    public boolean hasWriteAccess(Identity identity) {
        return getRights().checkWriteAccess(identity) || identity.isSuperuser();
    }

    public TextFile touch(String name, Identity identity, User owner, Group gowner) {
        if (getRights().checkWriteAccess(identity) && !files.containsKey(name)) {
            TextFile file = new TextFile(this, owner, gowner);
            files.put(name, file);
            return file;
        } else {
            return null;
        }
    }

    public Directory mkdir(String name, Identity identity, User owner, Group gowner) {
        if (getRights().checkWriteAccess(identity) && !files.containsKey(name)) {
            Directory dir = new Directory(this, owner, gowner);
            files.put(name, dir);
            return dir;
        } else {
            return null;
        }
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
        elements.sort((p1, p2) -> p1.getFirst().compareToIgnoreCase(p2.getFirst()));
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

    @Override
    public void writeOutPayload(IFSSerializer serializer) {
        serializer.writeType("directory");
        StringBuilder contents = new StringBuilder();
        for (String key : files.keySet()) {
            File file = files.get(key);
            if (file.shouldBeSerialized()) {
                contents.append(file.getID());
                contents.append(":");
                contents.append(key);
                contents.append('\n');
                serializer.queue(file);
            }
        }
        serializer.writeBase64Encoded(contents.toString().getBytes(StandardCharsets.UTF_8));
    }
}
