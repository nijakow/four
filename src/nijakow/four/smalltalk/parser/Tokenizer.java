package nijakow.four.smalltalk.parser;

import nijakow.four.smalltalk.objects.STSymbol;

public class Tokenizer {
    private final CharacterStream stream;

    public Tokenizer(CharacterStream stream) {
        this.stream = stream;
    }

    private void skipWhitespace() {
        while (stream.hasNext() && Character.isWhitespace(stream.peek()))
            stream.read();
    }

    public Token nextToken2() {
        skipWhitespace();

        if (!stream.hasNext()) return new Token(TokenType.EOF, null, this);
        else if (stream.peeks("(")) return new Token(TokenType.LPAREN, this);
        else if (stream.peeks(")")) return new Token(TokenType.RPAREN, this);
        else if (stream.peeks("[")) return new Token(TokenType.LBRACK, this);
        else if (stream.peeks("]")) return new Token(TokenType.RBRACK, this);
        else if (stream.peeks("|")) return new Token(TokenType.BAR, this);
        else if (stream.peeks(".")) return new Token(TokenType.DOT, this);
        else if (stream.peeks(",")) return new Token(TokenType.COMMA, this);
        else if (stream.peeks(":")) return new Token(TokenType.COLON, this);
        else if (stream.peeks(";")) return new Token(TokenType.SEMICOLON, this);

        StringBuilder builder = new StringBuilder();
        while (stream.hasNext()) {
            char c = stream.peek();
            if (Character.isWhitespace(c) || c == '.' || c == ',' || c == ';' || c == ')' || c == ']')
                break;
            builder.append(c);
            stream.read();
        }

        final String str = builder.toString();

        if ("self".equals(str)) return new Token(TokenType.SELF, this);
        else return new Token(TokenType.SYMBOL, STSymbol.get(str), this);
    }

    public Token nextToken() {
        Token t = nextToken2();
        System.out.println(t.getType());
        return t;
    }
}
