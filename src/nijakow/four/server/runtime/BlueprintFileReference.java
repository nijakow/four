package nijakow.four.server.runtime;

import nijakow.four.server.runtime.nvfs.NVFileSystem;
import nijakow.four.server.runtime.objects.Blueprint;

public class BlueprintFileReference extends BlueprintReference {
    private final NVFileSystem fs;
    private final String path;

    public BlueprintFileReference(NVFileSystem fs, String path) {
        this.fs = fs;
        this.path = path;
    }

    @Override
    public Blueprint getBlueprint() {
        return fs.getBlueprint(path);
    }
}
