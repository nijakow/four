package nijakow.four.smalltalk.parser;

import nijakow.four.smalltalk.objects.STCharacter;
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

    private char readChar() {
        if (!stream.hasNext())
            return '\0';
        if (stream.peek() == '\\') {
            stream.read();
            if (!stream.hasNext())
                return '\\';
            char c = stream.read();
            switch (c) {
                case 'n': return '\n';
                case 'r': return '\r';
                case 't': return '\t';
                case 'e': return '\033';
                case 'b': return '\b';
                case 's': return ' ';
                default: return c;
            }
        }
        return stream.read();
    }

    private String readStringUntil(char end) {
        StringBuilder sb = new StringBuilder();
        while (stream.hasNext() && stream.peek() != end)
            sb.append(readChar());
        if (stream.hasNext())
            stream.read();
        return sb.toString();
    }

    public Token nextToken() {
        skipWhitespace();

        if (!stream.hasNext()) return new Token(TokenType.EOF, null, this);
        else if (stream.peeks("\"")) { /* Comment */ readStringUntil('\"'); return nextToken(); }
        else if (stream.peeks("$")) return new Token(TokenType.CHARACTER, STCharacter.get(readChar()), this);
        else if (stream.peeks("\'")) return new Token(TokenType.STRING, readStringUntil('\''), this);
        else if (stream.peeks("#\'")) return new Token(TokenType.SYMBOL, STSymbol.get(readStringUntil('\'')), this);
        else if (stream.peeks(":=")) return new Token(TokenType.ASSIGN, this);
        else if (stream.peeks("(")) return new Token(TokenType.LPAREN, this);
        else if (stream.peeks(")")) return new Token(TokenType.RPAREN, this);
        else if (stream.peeks("[")) return new Token(TokenType.LBRACK, this);
        else if (stream.peeks("]")) return new Token(TokenType.RBRACK, this);
        else if (stream.peeks("|")) return new Token(TokenType.BAR, this);
        else if (stream.peeks("^")) return new Token(TokenType.CARET, this);
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

        try {
            return new Token(TokenType.INTEGER, Integer.valueOf(str), this);
        } catch (NumberFormatException e) {
        }

        if ("self".equals(str)) return new Token(TokenType.SELF, this);
        else if ("super".equals(str)) return new Token(TokenType.SUPER, this);
        else if ("true".equals(str)) return new Token(TokenType.TRUE, this);
        else if ("false".equals(str)) return new Token(TokenType.FALSE, this);
        else if ("nil".equals(str)) return new Token(TokenType.NIL, this);
        else if ("thisContext".equals(str)) return new Token(TokenType.THISCONTEXT, this);
        else return new Token(TokenType.IDENTIFIER, STSymbol.get(str), this);
    }
}
