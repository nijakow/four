package nijakow.four.share.lang.base.parser;

import nijakow.four.share.util.Pair;

public interface StreamPosition {
	String getText();
	int getIndex();
	Pair<Integer, Integer> getPos();
	String makeErrorText(String message);
}