package nijakow.four.c.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class StringCharStream implements CharStream {
	private final Reader reader;

	@Override
	public int peek() {
		try {
			reader.mark(1);
			int c = reader.read();
			reader.reset();
			return c;
		} catch (IOException e) {
			return -1;
		}
	}

	@Override
	public int next() {
		try {
			return reader.read();
		} catch (IOException e) {
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
			reader.mark(1);
			for (int c = 0; c < s.length(); c++) {
				if (reader.read() != s.charAt(c)) {
					reader.reset();
					return false;
				}
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	public StringCharStream(String in) {
		this.reader = new StringReader(in);
	}

}
