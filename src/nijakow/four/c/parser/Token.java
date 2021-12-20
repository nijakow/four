package nijakow.four.c.parser;

public class Token {
	private final Tokenizer tokenizer;
	private final TokenType type;
	private final Object payload;
	
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
	
	public Token(Tokenizer tokenizer, TokenType type) {
		this(tokenizer, type, null);
	}
	
	public Token(Tokenizer tokenizer, TokenType type, Object payload) {
		this.tokenizer = tokenizer;
		this.type = type;
		this.payload = payload;
	}
}
