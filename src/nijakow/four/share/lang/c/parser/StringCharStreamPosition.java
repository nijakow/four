package nijakow.four.share.lang.c.parser;

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

}
