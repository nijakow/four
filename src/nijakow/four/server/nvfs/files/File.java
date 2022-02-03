package nijakow.four.server.nvfs.files;

import nijakow.four.server.nvfs.FileParent;
import nijakow.four.server.serialization.fs.IFSSerializer;
import nijakow.four.server.runtime.security.fs.FileAccessRights;
import nijakow.four.server.runtime.security.users.Group;
import nijakow.four.server.runtime.security.users.Identity;
import nijakow.four.server.runtime.security.users.User;
import nijakow.four.server.serialization.base.ISerializable;
import nijakow.four.server.serialization.base.ISerializer;

import java.util.UUID;

public abstract class File implements ISerializable {
    private final UUID uuid;
    private FileParent parent;
    private FileAccessRights rights;

    protected File(FileParent parent, User owner, Group gowner) {
        this.uuid = UUID.randomUUID();
        this.parent = parent;
        this.rights = new FileAccessRights(owner, gowner);
    }

    protected void serializeCore(ISerializer serializer) {
        serializer.openProperty("file.name").writeString(getName()).close();
        serializer.openProperty("file.path").writeString(getFullName()).close();
        // TODO: Permissions!
    }

    public final void writeOut(IFSSerializer serializer) {
        serializer.newEntry(getID());
        serializer.writeOwner(rights.getUserAccessRights().getIdentity().getName());
        serializer.writeGroup(rights.getGroupAccessRights().getIdentity().getName());
        serializer.writePermissions(getmod());
        writeOutPayload(serializer);
    }

    public abstract void writeOutPayload(IFSSerializer serializer);

    public String getID() { return uuid.toString(); }

    protected FileParent getParent() { return parent; }
    public File getRoot() { return getParent().getRoot(); }

    public TextFile asTextFile() { return null; }
    public Directory asDirectory() { return null; }

    public FileAccessRights getRights() { return this.rights; }

    public boolean chown(Identity actor, Identity newOwner) {
        if (!getRights().getUserAccessRights().getIdentity().includes(actor) || newOwner.asUser() == null)
            return false;
        getRights().getUserAccessRights().setIdentity(newOwner.asUser());
        return true;
    }

    public boolean chgrp(Identity actor, Identity newOwner) {
        if (!getRights().getUserAccessRights().getIdentity().includes(actor) || newOwner.asGroup() == null)
            return false;
        getRights().getGroupAccessRights().setIdentity(newOwner.asGroup());
        return true;
    }

    public int getmod() {
        int flags = 0b000000000;
        if (getRights().getUserAccessRights().isReadable())    flags |= 0b100000000;
        if (getRights().getUserAccessRights().isWritable())    flags |= 0b010000000;
        if (getRights().getUserAccessRights().isExecutable())  flags |= 0b001000000;
        if (getRights().getGroupAccessRights().isReadable())   flags |= 0b000100000;
        if (getRights().getGroupAccessRights().isWritable())   flags |= 0b000010000;
        if (getRights().getGroupAccessRights().isExecutable()) flags |= 0b000001000;
        if (getRights().getOtherAccessRights().isReadable())   flags |= 0b000000100;
        if (getRights().getOtherAccessRights().isWritable())   flags |= 0b000000010;
        if (getRights().getOtherAccessRights().isExecutable()) flags |= 0b000000001;
        return flags;
    }

    public boolean chmod(Identity identity, int flags) {
        if (!getRights().getUserAccessRights().getIdentity().includes(identity))
            return false;
        getRights().getUserAccessRights().setReadable(   (flags & 0b100000000) != 0);
        getRights().getUserAccessRights().setWritable(   (flags & 0b010000000) != 0);
        getRights().getUserAccessRights().setExecutable( (flags & 0b001000000) != 0);
        getRights().getGroupAccessRights().setReadable(  (flags & 0b000100000) != 0);
        getRights().getGroupAccessRights().setWritable(  (flags & 0b000010000) != 0);
        getRights().getGroupAccessRights().setExecutable((flags & 0b000001000) != 0);
        getRights().getOtherAccessRights().setReadable(  (flags & 0b000000100) != 0);
        getRights().getOtherAccessRights().setWritable(  (flags & 0b000000010) != 0);
        getRights().getOtherAccessRights().setExecutable((flags & 0b000000001) != 0);
        return true;
    }

    public void forceRM() { getParent().remove(this); }
    public void forceMoveTo(FileParent parent, String name) {
        if (parent == null)
            forceRM();
        else if (parent == getParent())
            getParent().rename(this, name);
        else if (parent.add(this, name)) {
            forceRM();
            this.parent = parent;
        }
    }

    public boolean rm(Identity identity) {
        if (getParent().hasWriteAccess(identity)) {
            forceRM();
            return true;
        } else {
            return false;
        }
    }

    public boolean moveTo(FileParent parent, String name, Identity identity) {
        if (getParent().hasWriteAccess(identity) && parent.hasWriteAccess(identity)) {
            forceMoveTo(parent, name);
            return true;
        } else {
            return false;
        }
    }

    public String getName() {
        return getParent().getMyName(this);
    }

    public String getFullName() {
        return getParent().getMyFullName(this);
    }

    public File resolve1(String name) {
        if ("".equals(name)) return this;
        else if (".".equals(name)) return this;
        else if ("..".equals(name)) return getParent().asFile();
        else return null;
    }

    public File resolve(String path) {
        if (path.startsWith("/"))
            return getRoot().resolve(path.substring(1));

        File current = this;
        String[] tokens = path.split("/");

        for (String token : tokens) {
            if (current == null)
                break;
            current = current.resolve1(token);
        }

        return current;
    }
}
