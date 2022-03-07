package nijakow.four.server.runtime.security.users;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User extends Identity {
    private byte[] passwordHash;
    private String shell;
    private long lastActive = 0;

    protected User(IdentityDatabase db, String name) {
        super(db, name);
        this.passwordHash = null;
        this.shell = null;
    }

    @Override
    public User asUser() {
        return this;
    }

    public byte[] getPasswordHash() {
        if (this.passwordHash == null)
            return null;
        return this.passwordHash.clone();
    }

    public void setPassword(String password) {
        if (password == null)
            setPasswordHash(null);
        else {
            byte[] hashed = hash(password);
            if (hashed != null)
                setPasswordHash(hashed);
        }
    }
    public void setPasswordHash(byte[] hash) {
        if (hash == null)
            this.passwordHash = null;
        else
            this.passwordHash = hash.clone();
    }

    public void setPasswordHashIfNotSet(byte[] hash) {
        if (this.passwordHash == null)
            this.passwordHash = hash.clone();
    }

    public boolean checkPassword(String password) {
        byte[] ourpass = getPasswordHash();
        if (ourpass == null)
            return false;
        byte[] hashed = hash(password);
        if (hashed == null)
            return false;
        if (hashed.length != ourpass.length)
            return false;
        for (int i = 0; i < ourpass.length; i++)
            if (ourpass[i] != hashed[i])
                return false;
        return true;
    }

    public String getShell() { return this.shell; }

    public void setShell(String shell) { this.shell = shell; }


    private static byte[] hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            return md.digest(text.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public boolean isCurrentlyActive() {
        return System.currentTimeMillis() - lastActive < (10*60) * 1000;
    }

    public void notifyActive() {
        lastActive = System.currentTimeMillis();
    }
}
