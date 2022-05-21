package nijakow.four.smalltalk.vm;

import nijakow.four.smalltalk.objects.STInstance;

public class Context {
    private final STInstance self;

    public Context(STInstance self) {
        this.self = self;
    }

    public STInstance getSelf() {
        return this.self;
    }
}
