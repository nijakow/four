package nijakow.four.runtime.nvfs;

import nijakow.four.runtime.Blue;
import nijakow.four.runtime.Blueprint;

public class SharedTextFileState extends SharedFileState {
    private Blue blue;

    public SharedTextFileState() {
    }

    public Blue getBlue() {
        return blue;
    }
    public void updateBlueprint(Blueprint blueprint) { blue.updateBlueprint(blueprint); }
}
