package nijakow.four.smalltalk.parser;

public class Token {
    private final TokenType type;
    private final Object payload;
    private final Tokenizer tokenizer;
    private Token cachedNextToken;

    public Token(TokenType type, Object payload, Tokenizer tokenizer) {
        this.type = type;
        this.payload = payload;
        this.tokenizer = tokenizer;
        this.cachedNextToken = null;
    }

    public Token(TokenType type, Tokenizer tokenizer) {
        this(type, null, tokenizer);
    }

    public Token next() {
        if (this.cachedNextToken == null)
            this.cachedNextToken = tokenizer.nextToken();
        return this.cachedNextToken;
    }

    public TokenType getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
