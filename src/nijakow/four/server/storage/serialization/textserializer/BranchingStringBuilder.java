package nijakow.four.server.storage.serialization.textserializer;

import java.util.ArrayList;
import java.util.List;

public class BranchingStringBuilder extends AbstractStringBuilder {
    private final List<AbstractStringBuilder> builders;

    public BranchingStringBuilder() {
        this.builders = new ArrayList<>();
        this.builders.add(new ConcreteStringBuilder());
    }

    @Override
    public String asString() {
        StringBuilder theBuilder = new StringBuilder();
        for (AbstractStringBuilder builder : builders)
            theBuilder.append(builder.asString());
        return theBuilder.toString();
    }

    @Override
    public void append(char c) {
        builders.get(builders.size() - 1).append(c);
    }

    public BranchingStringBuilder branch() {
        BranchingStringBuilder builder = new BranchingStringBuilder();
        builders.add(builder);
        builders.add(new ConcreteStringBuilder());
        return builder;
    }
}
