package nijakow.four.smalltalk.parser;

public class StringCharacterStream implements CharacterStream {
    private final String string;
    private int index;

    public StringCharacterStream(String string) {
        this.string = string;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < string.length();
    }

    @Override
    public char peek() {
        if (!hasNext())
            return '\0';
        return string.charAt(index);
    }

    @Override
    public char read() {
        if (!hasNext())
            return '\0';
        return string.charAt(index++);
    }

    @Override
    public boolean peeks(String pattern) {
        int prevIndex = index;
        for (int i = 0; i < pattern.length(); i++) {
            if (!hasNext() || peek() != pattern.charAt(i)) {
                index = prevIndex;
                return false;
            }
            read();
        }
        return true;
    }

    @Override
    public StreamPosition getPosition() {
        return new StringCharacterStreamPosition(this, "", string, index);
    }
}
