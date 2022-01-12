package nijakow.four.c.parser;

import nijakow.four.FourException;

public class ParseException extends FourException {
	private final StreamPosition position;
	private final String message;
	
	public ParseException(StreamPosition position, String message) {
		this.position = position;
		this.message = message;
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
		return this.message + " => " + sb.toString().replaceAll("\\s+", " ");
	}
}
