package nijakow.four.server.runtime.security.users;

import java.util.HashMap;
import java.util.Map;

public class IdentityDatabase {
    private final Map<String, Identity> identities = new HashMap<>();
    private final Group usersGroup;
    private final User rootUser;
    private final Group rootGroup;

    public IdentityDatabase() {
        this.usersGroup = newGroup("users");
        this.rootUser = newUser("root");
        this.rootGroup = newGroup("admin");
        this.rootGroup.add(this.rootUser);
    }

    static String newID() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < 16; x++)
            builder.append(chars.charAt((int) (Math.random() * chars.length())));
        return builder.toString();
    }

    void add(Identity identity) {
        identities.put(identity.getID(), identity);
    }

    public Identity find(String id) { return identities.get(id); }

    public User getNewUnprivilegedUser() {
        return new User(this, newID());
    }

    public User newUser(String name) {
        User user = new User(this, name);
        getUsersGroup().add(user);
        return user;
    }
    public Group newGroup(String name) { return new Group(this, name); }

    public User getRootUser() {
        return rootUser;
    }

    public Group getRootGroup() {
        return rootGroup;
    }

    public Group getUsersGroup() {
        return usersGroup;
    }
}
