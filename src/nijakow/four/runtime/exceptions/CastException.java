package nijakow.four.runtime.exceptions;

import nijakow.four.runtime.objects.Instance;
import nijakow.four.runtime.types.Type;

public class CastException extends FourRuntimeException {

    public CastException(Type type, Instance instance) {
        super("Can't cast " + instance + " to " + type + "!");
    }
}
