package nijakow.four.smalltalk.parser;

import nijakow.four.smalltalk.vm.FourException;

public class ParseException extends FourException {
    private final Token token;

    public ParseException(Token token, String message) {
        super(message);
        this.token = token;
    }
}
