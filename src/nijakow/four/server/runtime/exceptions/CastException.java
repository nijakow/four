package nijakow.four.server.runtime.exceptions;

import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.types.Type;

public class CastException extends FourRuntimeException {

    public CastException(Type type, Instance instance) {
        super("Can't cast " + instance + " to " + type + "!");
    }
}
