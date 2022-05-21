package nijakow.four.smalltalk.parser;

import nijakow.four.smalltalk.objects.STSymbol;

public class Tokenizer {
    private final CharacterStream stream;

    public Tokenizer(CharacterStream stream) {
        this.stream = stream;
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(stream.peek()))
            stream.read();
    }

    public Token nextToken() {
        skipWhitespace();

        if (!stream.hasNext()) return new Token(TokenType.EOF, null, this);
        else if (stream.peeks("(")) return new Token(TokenType.LBRACK, this);
        else if (stream.peeks(")")) return new Token(TokenType.RBRACK, this);
        else if (stream.peeks("|")) return new Token(TokenType.BAR, this);
        else if (stream.peeks(".")) return new Token(TokenType.BAR, this);
        else if (stream.peeks(",")) return new Token(TokenType.BAR, this);
        else if (stream.peeks(":")) return new Token(TokenType.BAR, this);
        else if (stream.peeks(";")) return new Token(TokenType.BAR, this);

        StringBuilder builder = new StringBuilder();
        while (true) {
            char c = stream.peek();
            if (Character.isWhitespace(c))
                break;
            builder.append(c);
            if (!Character.isAlphabetic(c) && !Character.isDigit(c))
                break;
        }

        final String str = builder.toString();

        if ("self".equals(str)) return new Token(TokenType.SELF, this);
        else return new Token(TokenType.SYMBOL, STSymbol.get(str), this);
    }
}
