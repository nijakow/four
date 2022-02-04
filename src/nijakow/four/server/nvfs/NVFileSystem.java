package nijakow.four.server.nvfs;

import nijakow.four.server.serialization.fs.IFSSerializer;
import nijakow.four.server.runtime.objects.blue.Blueprint;
import nijakow.four.server.runtime.security.users.Group;
import nijakow.four.server.runtime.security.users.Identity;
import nijakow.four.server.runtime.security.users.IdentityDatabase;
import nijakow.four.server.runtime.security.users.User;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.nvfs.files.Directory;
import nijakow.four.server.nvfs.files.File;
import nijakow.four.server.nvfs.files.TextFile;
import nijakow.four.server.runtime.objects.blue.Blue;
import nijakow.four.server.serialization.base.ISerializable;
import nijakow.four.server.serialization.base.ISerializer;
import nijakow.four.share.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class NVFileSystem implements FileParent, ISerializable {
    private Directory root;

    public NVFileSystem(IdentityDatabase db) {
        this.root = new Directory(this, db.getRootUser(), db.getRootGroup());
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

    @Override
    public boolean hasWriteAccess(Identity identity) {
        return getRoot().hasWriteAccess(identity);
    }

    public Directory getRoot() {
        return root;
    }
    public void setRoot(Directory dir) { this.root = dir; }

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

    public TextFile touch(String file, Identity identity, User owner, Group gowner) {
        Pair<String, String> path = splitPath(file);
        return resolve(path.getFirst()).asDirectory().touch(path.getSecond(), identity, owner, gowner);
    }

    public Directory mkdir(String file, Identity identity, User owner, Group gowner) {
        Pair<String, String> path = splitPath(file);
        return resolve(path.getFirst()).asDirectory().mkdir(path.getSecond(), identity, owner, gowner);
    }

    public boolean mv(String file, String loc, Identity identity) {
        File f = resolve(file);
        File target = resolve(loc);

        if (f != null) {
            if (target != null) {
                if (target.asDirectory() == null)
                    return false;
                f.moveTo(target.asDirectory(), f.getName(), identity);
            } else {
                Pair<String, String> path = splitPath(loc);
                target = resolve(path.getFirst());
                if (target == null || target.asDirectory() == null)
                    return false;
                f.moveTo(target.asDirectory(), path.getSecond(), identity);
            }
            return true;
        }
        return false;
    }

    public boolean chmod(String path, int flags, Identity identity) {
        File file = resolve(path);
        return (file != null && file.chmod(identity, flags));
    }

    public boolean chown(String path, Identity newOwner, Identity actor) {
        File file = resolve(path);
        return (file != null && file.chown(actor, newOwner));
    }

    public boolean chgrp(String path, Identity newGroup, Identity actor) {
        File file = resolve(path);
        return (file != null && file.chown(actor, newGroup));
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

    public void load(java.io.File file, String path, IdentityDatabase db) {
        final String name = file.getName();
        final String newPath = path + "/" + name;

        final boolean secure = newPath.equals("/") || newPath.startsWith("/secure");
        final User user = db.getRootUser();
        final Group group = secure ? db.getRootGroup() : db.getUsersGroup();

        if (file.isDirectory()) {
            mkdir(newPath, user, user, group);
            for (java.io.File f : file.listFiles()) {
                load(f, newPath, db);
            }
        } else if (file.isFile()) {
            TextFile textFile = touch(newPath, user, user, group);
            textFile.setContents(getResourceText(file));
        }
    }

    public void load(java.io.File file, IdentityDatabase db) throws CompilationException, ParseException {
        for (java.io.File f : file.listFiles()) {
            load(f, "", db);
        }
    }

    public Blue getBlue(String name) throws CompilationException, ParseException {
        TextFile file = resolveTextFile(name);
        if (file == null)
            return null;
        file.ensureCompiled();
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

    public void writeOut(IFSSerializer serializer) {
        serializer.queue(getRoot());
    }
}
