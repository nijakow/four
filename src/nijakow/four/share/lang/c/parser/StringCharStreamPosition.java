package nijakow.four.share.lang.c.parser;

import nijakow.four.share.util.Pair;

public class StringCharStreamPosition implements StreamPosition {
	private final StringCharStream stream;
	private final String text;
	private final int index;
	
	public StringCharStreamPosition(StringCharStream stream, String text, int index) {
		this.stream = stream;
		this.text = text;
		this.index = index;
	}
	
	@Override
	public String getText() {
		return text;
	}

	@Override
	public int getIndex() {
		return index;
	}

	public Pair<Integer, Integer> getPos() {
		int l = 1;
		int c = 1;
		int i = 0;
		for (i = 0; i < index && i < text.length(); i++) {
			final char p = text.charAt(i);
			if (p == '\n') {
				l++;
				c = 1;
			} else {
				c++;
			}
		}
		return new Pair(l, c);
	}
}
