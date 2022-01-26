package nijakow.four.server.runtime.security.users;

public abstract class Identity {
    private final IdentityDatabase db;
    private final String id;

    protected Identity(IdentityDatabase db) {
        this.db = db;
        this.id = db.newID();
        db.add(this);
    }

    protected IdentityDatabase getDB() { return db; }

    public String getID() { return id; }

    public boolean includes(Identity identity) { return identity == this; }
}
