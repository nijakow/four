package nijakow.four.server.runtime;

import nijakow.four.server.runtime.objects.Blueprint;

public class BlueprintKeyReference extends BlueprintReference {
    private final Key key;

    public BlueprintKeyReference(Key key) {
        this.key = key;
    }

    @Override
    public Blueprint getBlueprint() {
        return key.getBlueprint();
    }
}
