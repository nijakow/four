package nijakow.four.smalltalk.parser;

import nijakow.four.smalltalk.util.Pair;

public interface StreamPosition {
    String getText();
    int getIndex();
    Pair<Integer, Integer> getPos();
    String makeErrorText(String message);
}
