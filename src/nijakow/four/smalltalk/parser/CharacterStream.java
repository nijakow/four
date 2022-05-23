package nijakow.four.smalltalk.parser;

public interface CharacterStream {
    boolean hasNext();
    char peek();
    char read();
    boolean peeks(String pattern);
    StreamPosition getPosition();
}
