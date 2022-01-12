package nijakow.four.runtime.vm;

import nijakow.four.runtime.FMapping;

public class SharedFiberState {
    private final FMapping statics = new FMapping();

    public FMapping getStatics() { return statics; }
}
