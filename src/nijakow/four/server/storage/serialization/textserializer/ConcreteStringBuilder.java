package nijakow.four.server.storage.serialization.textserializer;

public class ConcreteStringBuilder extends AbstractStringBuilder {
    private final StringBuilder builder;

    public ConcreteStringBuilder() {
        this.builder = new StringBuilder();
    }

    @Override
    public String asString() {
        return builder.toString();
    }

    @Override
    public void append(char c) {
        builder.append(c);
    }
}
