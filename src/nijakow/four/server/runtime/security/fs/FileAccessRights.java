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

    public boolean checkReadAccess(Identity identity) {
        return userAccessRights.checkReadAccess(identity)
                || groupAccessRights.checkReadAccess(identity)
                || otherAccessRights.isReadable();
    }

    public boolean checkWriteAccess(Identity identity) {
        return userAccessRights.checkWriteAccess(identity)
                || groupAccessRights.checkWriteAccess(identity)
                || otherAccessRights.isWritable();
    }

    public boolean checkExecuteAccess(Identity identity) {
        return userAccessRights.checkExecuteAccess(identity)
                || groupAccessRights.checkExecuteAccess(identity)
                || otherAccessRights.isExecutable();
    }
}
