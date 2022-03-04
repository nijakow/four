package nijakow.four.share.lang.c.parser;

import nijakow.four.share.lang.FourCompilerException;
import nijakow.four.share.util.Pair;

public class ParseException extends FourCompilerException {
	public ParseException(StreamPosition position, String message) {
		super(position, message);
	}

	public ParseException(Token token, String message) {
		this(token.getPosition(), message);
	}
}
