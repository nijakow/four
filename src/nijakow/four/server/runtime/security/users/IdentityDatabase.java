package nijakow.four.server.runtime.security.users;

import java.util.HashMap;
import java.util.Map;

public class IdentityDatabase {
    private final Map<String, Identity> identities = new HashMap<>();

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

    public User newUser() {
        return new User(this);
    }

    public User getNewUnprivilegedUser() {
        return newUser();
    }

    public User getRootUser() {
        return null;    // TODO
    }

    public Group getRootGroup() {
        // TODO
        return null;
    }

    public Group getUsersGroup() {
        // TODO
        return null;
    }
}
