package nijakow.four.server.runtime.security.fs;

import nijakow.four.server.runtime.security.users.Group;
import nijakow.four.server.runtime.security.users.Identity;
import nijakow.four.server.runtime.security.users.User;

public class FileAccessRights {
    private final AccessRights<User> userAccessRights;
    private final AccessRights<Group> groupAccessRights;
    private final Permissions otherAccessRights;

    public FileAccessRights(AccessRights<User> userAccessRights, AccessRights<Group> groupAccessRights, Permissions otherAccessRights) {
        this.userAccessRights = userAccessRights;
        this.groupAccessRights = groupAccessRights;
        this.otherAccessRights = otherAccessRights;
    }

    public FileAccessRights(User user, Group group) {
        this(new AccessRights<User>(user), new AccessRights<Group>(group), new Permissions(true, false, false));
    }

    public AccessRights<User> getUserAccessRights() { return userAccessRights; }
    public AccessRights<Group> getGroupAccessRights() { return groupAccessRights; }
    public Permissions getOtherAccessRights() { return otherAccessRights; }

    public boolean checkReadAccess(Identity identity) {
        return userAccessRights.checkReadAccess(identity)
                || groupAccessRights.checkReadAccess(identity)
                || otherAccessRights.isReadable()
                || identity.isSuperuser();
    }

    public boolean checkWriteAccess(Identity identity) {
        return userAccessRights.checkWriteAccess(identity)
                || groupAccessRights.checkWriteAccess(identity)
                || otherAccessRights.isWritable()
                || identity.isSuperuser();
    }

    public boolean checkExecuteAccess(Identity identity) {
        return userAccessRights.checkExecuteAccess(identity)
                || groupAccessRights.checkExecuteAccess(identity)
                || otherAccessRights.isExecutable()
                || identity.isSuperuser();
    }
}
