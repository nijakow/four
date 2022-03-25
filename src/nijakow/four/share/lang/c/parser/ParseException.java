package nijakow.four.share.lang.c.parser;

import nijakow.four.share.lang.FourCompilerException;
import nijakow.four.share.util.Pair;

public class ParseException extends FourCompilerException {
	private final Token token;

	public ParseException(StreamPosition position, String message) {
		super(position, message);
		this.token = null;
	}

	public ParseException(Token token, String message) {
		super(token.getPosition(), message);
		this.token = token;
	}

	public Token getToken() {
		return this.token;
	}
}
