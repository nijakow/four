package nijakow.four.server.runtime.nvfs.shared;

import nijakow.four.server.runtime.objects.Blue;
import nijakow.four.server.runtime.objects.Blueprint;

public class SharedTextFileState extends SharedFileState {
    private Blue blue;

    public SharedTextFileState() {
        this.blue = new Blue();
    }

    public Blue getBlue() {
        return blue;
    }

    public void updateBlueprint(Blueprint blueprint) { blue.updateBlueprint(blueprint); }
}
