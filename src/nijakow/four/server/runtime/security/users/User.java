package nijakow.four.server.runtime.security.users;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class User extends Identity {
    private String password;
    private String shell;

    protected User(IdentityDatabase db, String name) {
        super(db, name);
        this.password = null;
        this.shell = null;
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

    public String getPasswordHash() {
        if (this.password == null)
            return "";  // TODO, FIXME, XXX: This is a security issue!
        return this.password;
        /*if (this.password == null)
            return "";
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] hashedPassword = md.digest(this.password.getBytes(StandardCharsets.UTF_8));
            byte[] both = new byte[salt.length + hashedPassword.length];
            for (int i = 0; i < salt.length; i++)
                both[i] = salt[i];
            for (int i = 0; i < hashedPassword.length; i++)
                both[salt.length + i] = hashedPassword[i];
            return Base64.getEncoder().encodeToString(both);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }*/
    }

    public String getShell() { return this.shell; }

    public void setShell(String shell) { this.shell = shell; }
}
