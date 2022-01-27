package nijakow.four.server.runtime.security.users;

public abstract class Identity {
    private final IdentityDatabase db;
    private final String id;
    private final String name;

    protected Identity(IdentityDatabase db, String name) {
        this.db = db;
        this.id = db.newID();
        this.name = name;
        db.add(this);
    }

    protected IdentityDatabase getDB() { return db; }
    public String getID() { return id; }
    public String getName() { return name; }

    public User asUser() { return null; }
    public Group asGroup() { return null; }

    public boolean includes(Identity identity) { return identity == this; }
}