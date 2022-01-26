package nijakow.four.server.runtime.vm;

import nijakow.four.server.runtime.objects.collections.FMapping;

public class SharedFiberState {
    private final FMapping statics = new FMapping();

    public FMapping getStatics() { return statics; }
}
