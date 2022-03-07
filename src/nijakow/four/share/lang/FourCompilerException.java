package nijakow.four.share.lang;

import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.share.lang.c.parser.StreamPosition;

public class FourCompilerException extends FourRuntimeException {
    private String errorText;

    public FourCompilerException(StreamPosition pos, String message) {
        super(message);
        this.errorText = (pos == null) ? message : pos.makeErrorText(message);
    }

    public String getErrorText() {
        return this.errorText;
    }
}
