package nijakow.four.runtime;

public class CastException extends FourRuntimeException {

    public CastException(Type type, Instance instance) {
        super("Can't cast " + instance + " to " + type + "!");
    }
}
