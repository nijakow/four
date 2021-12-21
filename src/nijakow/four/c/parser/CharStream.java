package nijakow.four.c.parser;

public interface CharStream {
	int peek();
	int next();
	void advance();
	boolean peeks(String s);
	StreamPosition getPosition();
}
