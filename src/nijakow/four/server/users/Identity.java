package nijakow.four.server.users;

import java.util.HashSet;
import java.util.Set;

public abstract class Identity {
    private final IdentityDatabase db;
    private final String name;
    private final Set<Group> groups = new HashSet<>();

    protected Identity(IdentityDatabase db, String name) {
        this.db = db;
        this.name = name;
        db.add(this);
    }

    protected IdentityDatabase getDB() { return db; }
    public String getName() { return name; }

    public User asUser() { return null; }
    public Group asGroup() { return null; }
    public boolean isSuperuser() { return this == db.getRootUser(); }

    void inGroup(Group g) { groups.add(g); }
    void notInGroup(Group g) { groups.remove(g); }

    public boolean includes(Identity identity) { return identity == this; }

    public void unlink() {
        for (Group g : groups.toArray(new Group[0]))
            g.remove(this);
    }
}
