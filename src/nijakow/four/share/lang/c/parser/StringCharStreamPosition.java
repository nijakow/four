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

	public String makeErrorText(String message) {
		StreamPosition pos = this;
		StringBuilder sb = new StringBuilder();
		if (pos == null) {
			sb.append("?:?: ");
			sb.append(message);
		} else {
			String text = pos.getText();
			Pair<Integer, Integer> lp = pos.getPos();

			int l = 1;
			int c = 1;
			boolean newline = true;
			for (int index = 0; index < text.length(); index++) {
				if (l > lp.getFirst() - 5 && l < lp.getFirst() + 5) {
					if (newline)
						sb.append(String.format("%04d  ", l));
					newline = false;
					sb.append(text.charAt(index));
				}
				if (text.charAt(index) == '\n') {
					newline = true;
					if (l == lp.getFirst()) {
						sb.append("      ");    // Line spacing
						for (int xx = 1; xx < lp.getSecond(); xx++)
							sb.append(' ');
						sb.append('^');
						sb.append(' ');
						sb.append(lp.getFirst() + ":" + lp.getSecond() + ": ");
						sb.append(message);
						sb.append('\n');
					}
					l++;
					c = 1;
				} else {
					c++;
				}
			}
		}
		return sb.toString();
	}
}
