package nijakow.four.server.runtime.security.users;

import java.nio.charset.StandardCharsets;
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
        if (getIdentityByName(name) != null)
            return null;
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

    public Identity getIdentityByName(String username) {
        for (Identity identity : identities.values()) {
            if (username.equals(identity.getName()))
                return identity;
        }
        return null;
    }

    public User login(String username, String password) {
        Identity identity = getIdentityByName(username);
        User user = (identity != null) ? identity.asUser() : null;
        return (user != null && user.checkPassword(password)) ? user : null;
    }

    public byte[] serializeAsBytes() {
        StringBuilder builder = new StringBuilder();
        for (Identity identity : identities.values()) {
            builder.append(identity.getID());
            builder.append(',');
            builder.append(identity.getName());
            User user = identity.asUser();
            if (user != null) {
                builder.append(",user,");
                builder.append(user.getPasswordHash());
            } else if (identity.asGroup() != null) {
                builder.append(",group");
            } else {
                builder.append(",unknown");
            }
            builder.append('\n');
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public Identity[] getIdentities() {
        return identities.values().toArray(new Identity[0]);
    }
}
