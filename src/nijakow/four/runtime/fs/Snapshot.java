package nijakow.four.runtime.fs;

public class Snapshot {
    private final int number;
    private final Layer layer;

    Snapshot(int number, Layer layer) {
        this.number = number;
        this.layer = layer;
    }

    Layer getLayer() {
        return layer;
    }

    int getNumber() {
        return number;
    }
}
