package nijakow.four.server.users;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class IdentityDatabase {
    private final Set<Identity> identities = new HashSet<>();
    private final Group usersGroup;
    private final User rootUser;
    private final Group rootGroup;
    private final User unprivilegedUser;

    public IdentityDatabase() {
        this.usersGroup = newGroup("users");
        this.rootUser = newUser("root");
        this.rootGroup = newGroup("gods");
        this.rootGroup.add(this.rootUser);
        this.unprivilegedUser = newUser("nouser");
    }

    static String newID() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-";
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < 16; x++)
            builder.append(chars.charAt((int) (Math.random() * chars.length())));
        return builder.toString();
    }

    void add(Identity identity) {
        identities.add(identity);
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

    public User getUnprivilegedUser() {
        return unprivilegedUser;
    }

    public Group getRootGroup() {
        return rootGroup;
    }

    public Group getUsersGroup() {
        return usersGroup;
    }

    public Identity getIdentityByName(String username) {
        for (Identity identity : identities) {
            if (username.equals(identity.getName()))
                return identity;
        }
        return null;
    }

    public User getUserByName(String username) {
        Identity identity = getIdentityByName(username);
        return (identity == null) ? null : identity.asUser();
    }

    public Group getGroupByName(String username) {
        Identity identity = getIdentityByName(username);
        return (identity == null) ? null : identity.asGroup();
    }

    public User login(String username, String password) {
        Identity identity = getIdentityByName(username);
        User user = (identity != null) ? identity.asUser() : null;
        return (user != null && user.checkPassword(password)) ? user : null;
    }

    public byte[] serializeAsBytes() {
        StringBuilder builder = new StringBuilder();
        for (Identity identity : identities) {
            builder.append(identity.getName());
            User user = identity.asUser();
            if (user != null) {
                builder.append(",user,");
                byte[] hash = user.getPasswordHash();
                if (hash != null)
                    builder.append(new String(Base64.getEncoder().encode(hash), StandardCharsets.UTF_8));
            } else if (identity.asGroup() != null) {
                builder.append(",group,");
                boolean hasPrev = false;
                for (Identity i : identity.asGroup().getMembers()) {
                    if (hasPrev)
                        builder.append(':');
                    builder.append(i.getName());
                    hasPrev = true;
                }
            } else {
                builder.append(",unknown");
            }
            builder.append('\n');
        }
        return builder.toString().getBytes(StandardCharsets.UTF_8);
    }

    public Identity[] getIdentities() {
        return identities.toArray(new Identity[0]);
    }

    public void restore(String serialized) {
        List<Runnable> runLater = new ArrayList<>();
        if (serialized.isEmpty()) return;
        for (String line : serialized.split("\n")) {
            String[] toks = line.split(",");
            String name = toks[0];
            String type = toks[1];
            if ("user".equals(type)) {
                User user = getUserByName(name);
                if (user == null)
                    user = newUser(name);
                if (toks.length >= 3 && !toks[2].isEmpty())
                    user.setPasswordHashIfNotSet(Base64.getDecoder().decode(toks[2]));
            } else if ("group".equals(type)) {
                Group group = getGroupByName(name);
                if (group == null) group = newGroup(name);
                final Group theGroup = group;
                final String[] members = toks[2].split(":");
                runLater.add(() -> {
                    for (String member : members) {
                        theGroup.add(getIdentityByName(member));
                    }
                });
            }
        }
    }

    public boolean deleteIdentity(String name) {
        Identity identity = getIdentityByName(name);
        if (identity == getRootUser() || identity == getUnprivilegedUser() || identity == usersGroup || identity == rootGroup)
            return false;
        else if (identity != null) {
            identities.remove(identity);
            identity.unlink();
            return true;
        } else {
            return false;
        }
    }
}
