package nijakow.four.c.parser;

public class StringCharStream implements CharStream {
	private final String string;
	private int offset;

	@Override
	public int peek() {
		try {
			return string.charAt(offset);
		} catch (StringIndexOutOfBoundsException e) {
			return -1;
		}
	}

	@Override
	public int next() {
		try {
			return string.charAt(offset++);
		} catch (StringIndexOutOfBoundsException e) {
			return -1;
		}
	}

	@Override
	public void advance() {
		next();
	}

	@Override
	public boolean peeks(String s) {
		try {
			int c;
			for (c = 0; c < s.length(); c++) {
				if (string.charAt(offset + c) != s.charAt(c)) {
					return false;
				}
			}
			offset += c;
			return true;
		} catch (StringIndexOutOfBoundsException e) {
			return false;
		}
	}
	
	public StringCharStreamPosition getPosition() {
		return new StringCharStreamPosition(this, this.string, this.offset);
	}
	
	public StringCharStream(String in) {
		this.string = in;
		this.offset = 0;
	}

}
