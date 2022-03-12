package nijakow.four.server.storage.serialization.textserializer;

public abstract class AbstractStringBuilder {

    public abstract String asString();
    public abstract void append(char c);

    public void append(String s) {
        for (int i = 0; i < s.length(); i++)
            append(s.charAt(i));
    }

    public void append(int i) {
        append(Integer.toString(i));
    }
}
