package nijakow.four.lang.c.parser;

import nijakow.four.server.runtime.exceptions.FourRuntimeException;

public class ParseException extends FourRuntimeException {
	private final StreamPosition position;
	
	public ParseException(StreamPosition position, String message) {
		super(message);
		this.position = position;
	}

	public ParseException(Token token, String message) {
		this(token.getPosition(), message);
	}

	@Override
	public String getMessage() {
		StreamPosition pos = position;
		String text = pos.getText();
		StringBuilder sb = new StringBuilder();
		int min = -30;
		int max = 30;
		for (int x = min; x <= max; x++) {
			if (x == 0) sb.append(" --> ");
			char c;
			
			try {
				c = text.charAt(pos.getIndex() + x);
			} catch (StringIndexOutOfBoundsException e) {
				c = ' ';
			}
			if (c < 32 || c >= 127)
				c = ' ';
			sb.append(c);
			if (x == 0) sb.append(" <-- ");
		}
		return super.getMessage() + " => " + sb.toString().replaceAll("\\s+", " ");
	}
}
