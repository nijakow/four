package nijakow.four.smalltalk.parser;


public class Token {
    private final TokenType type;
    private final Object payload;
    private final Tokenizer tokenizer;
    private Token cachedNextToken;
    private final StreamPosition position;
    private final StreamPosition endPosition;

    public Token(TokenType type, Object payload, Tokenizer tokenizer, StreamPosition pos, StreamPosition endPos) {
        this.type = type;
        this.payload = payload;
        this.tokenizer = tokenizer;
        this.cachedNextToken = null;
        this.position = pos;
        this.endPosition = endPos;
    }

    public StreamPosition getPosition() {
        return position;
    }

    public StreamPosition getEndPosition() {
        return endPosition;
    }

    public Token(TokenType type, Tokenizer tokenizer, StreamPosition pos, StreamPosition endPos) {
        this(type, null, tokenizer, pos, endPos);
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
