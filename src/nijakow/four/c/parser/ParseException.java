package nijakow.four.c.parser;

public class ParseException extends Exception {
	private final Token token;
	private final String message;
	
	public ParseException(Token token, String message) {
		this.token = token;
		this.message = message;
	}

	@Override
	public String getMessage() {
		StreamPosition pos = token.getPosition();
		String text = pos.getText();
		StringBuilder sb = new StringBuilder();
		int min = -30;
		int max = +30;
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
		return this.message + " => " + sb.toString();
	}
}
