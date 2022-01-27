package nijakow.four.server.runtime.security.users;

public class User extends Identity {
    private String password;

    protected User(IdentityDatabase db, String name) {
        super(db, name);
        this.password = null;
    }

    @Override
    public User asUser() {
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkPassword(String password) {
        return this.password != null && this.password.equals(password);
    }
}
