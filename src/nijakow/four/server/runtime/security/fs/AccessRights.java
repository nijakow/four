package nijakow.four.server.runtime.security.fs;

import nijakow.four.server.runtime.security.users.Identity;

public class AccessRights<T extends Identity> extends Permissions {
    private T identity;

    public AccessRights(T identity, boolean readable, boolean writable, boolean executable) {
        super(readable, writable, executable);
        this.identity = identity;
    }

    public AccessRights(T identity) {
        this(identity, true, true, true);
    }

    public T getIdentity() { return identity; }
    public void setIdentity(T identity) { this.identity = identity; }

    public boolean checkReadAccess(Identity identity) { return this.identity.includes(identity) && super.isReadable(); }
    public boolean checkWriteAccess(Identity identity) { return this.identity.includes(identity) && super.isWritable(); }
    public boolean checkExecuteAccess(Identity identity) { return this.identity.includes(identity) && super.isExecutable(); }
}
