package nijakow.four.c.parser;

public class Token {
	private final Tokenizer tokenizer;
	private final StreamPosition position;
	private final TokenType type;
	private final Object payload;
	
	public StreamPosition getPosition() {
		return position;
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
	
	public Token(Tokenizer tokenizer, StreamPosition pos, TokenType type) {
		this(tokenizer, pos, type, null);
	}
	
	public Token(Tokenizer tokenizer, StreamPosition pos, TokenType type, Object payload) {
		this.tokenizer = tokenizer;
		this.position = pos;
		this.type = type;
		this.payload = payload;
	}
}
