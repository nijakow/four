package nijakow.four.server.runtime.security.users;

public class User extends Identity {

    protected User(IdentityDatabase db) {
        super(db);
    }

    @Override
    public User asUser() {
        return this;
    }
}
