package nijakow.four.share.lang.c.parser;

public class StringCharStream implements CharStream {
	private final String fileName;
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
		return new StringCharStreamPosition(this, this.fileName, this.string, this.offset);
	}
	
	public StringCharStream(String fileName, String in) {
		this.fileName = fileName;
		this.string = in;
		this.offset = 0;
	}
}
