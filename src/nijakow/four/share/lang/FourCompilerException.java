package nijakow.four.share.lang;

import nijakow.four.server.runtime.exceptions.FourRuntimeException;

public class FourCompilerException extends FourRuntimeException {
    private String errorText;

    public FourCompilerException(String errorText) {
        super("Compilation Error: " + errorText);
        this.errorText = errorText;
    }

    public String getErrorText() {
        return this.errorText;
    }
}
