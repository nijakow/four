package nijakow.four.share.lang.c.parser;

public class Token {
	private final Tokenizer tokenizer;
	private final StreamPosition position;
	private final StreamPosition endPosition;
	private final TokenType type;
	private final Object payload;
	
	public StreamPosition getPosition() {
		return position;
	}
	public StreamPosition getEndPosition() {
		return endPosition;
	}
	
	public boolean is(TokenType type) {
		return this.type == type;
	}
	
	public TokenType getType() {
		return type;
	}
	
	public Object getPayload() {
		return payload;
	}
	
	public void fail() {
		tokenizer.unread(this);
	}
	
	public Token(Tokenizer tokenizer, StreamPosition pos, StreamPosition endPos, TokenType type) {
		this(tokenizer, pos, endPos, type, null);
	}
	
	public Token(Tokenizer tokenizer, StreamPosition pos, StreamPosition endPos, TokenType type, Object payload) {
		this.tokenizer = tokenizer;
		this.position = pos;
		this.endPosition = endPos;
		this.type = type;
		this.payload = payload;
	}
}
