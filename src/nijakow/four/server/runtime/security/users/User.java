package nijakow.four.server.runtime.security.users;

public class User extends Identity {

    protected User(IdentityDatabase db, String name) {
        super(db, name);
    }

    @Override
    public User asUser() {
        return this;
    }

    public boolean checkPassword(String password) {
        return password.isEmpty();
    }
}
