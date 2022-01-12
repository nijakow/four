package nijakow.four.runtime;

import nijakow.four.FourException;

public class CastException extends FourRuntimeException {

    public CastException(Type type, Instance instance) {
        super("Can't cast " + instance + " to " + type + "!");
    }
}
