package nijakow.four.share.lang.base.parser;

public interface CharStream {
	int peek();
	int next();
	void advance();
	boolean peeks(String s);
	StreamPosition getPosition();
}
