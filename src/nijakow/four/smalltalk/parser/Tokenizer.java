package nijakow.four.smalltalk.parser;

import nijakow.four.smalltalk.objects.STCharacter;
import nijakow.four.smalltalk.objects.STSymbol;

public class Tokenizer {
    private final CharacterStream stream;
    private boolean enableComments = false;
    private boolean muffleSymbols = false;

    public Tokenizer(CharacterStream stream) {
        this.stream = stream;
    }

    private void skipWhitespace() {
        while (stream.hasNext() && Character.isWhitespace(stream.peek()))
            stream.read();
    }

    public void enableCommentTokens() {
        this.enableComments = true;
    }

    public void muffleSymbols() {
        this.muffleSymbols = true;
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
                case '{': return '\002';
                case '}': return '\003';
                case '\0': return '\\';
                default: return c;
            }
        }
        return stream.read();
    }

    private String readStringUntil(String end) {
        StringBuilder sb = new StringBuilder();
        while (stream.hasNext() && !stream.peeks(end))
            sb.append(readChar());
        return sb.toString();
    }

    private boolean isBreakChar(char c) {
        return Character.isWhitespace(c) || c == '.' || c == ',' || c == ';' || c == '(' || c == '[' || c == '{' || c == ')' || c == ']' || c == '}' || c > 0x7f;
    }

    private String readSymbolLiteral() {
        StringBuilder sb = new StringBuilder();
        while (stream.hasNext() && !isBreakChar(stream.peek()))
            sb.append(readChar());
        return sb.toString();
    }

    public Token nextToken() {
        skipWhitespace();

        StreamPosition start = stream.getPosition();
        if (!stream.hasNext()) return new Token(TokenType.EOF, null, this, start, stream.getPosition());
        else if (stream.peeks("\"\"\"")) {
            /* Documentar */
            final String comment = readStringUntil("\"\"\"");
            return new Token(TokenType.DOCUMENTAR, comment, this, start, stream.getPosition());
        }
        else if (stream.peeks("\"")) {
            /* Comment */
            final String comment = readStringUntil("\"");
            return enableComments ? new Token(TokenType.COMMENT, comment, this, start, stream.getPosition()) : nextToken();
        }
        else if (stream.peeks("<primitive:")) {
            /* Primitive */
            final String name = readStringUntil(">");
            return new Token(TokenType.PRIMITIVE, STSymbol.get(name.trim()), this, start, stream.getPosition());
        }
        else if (stream.peeks("$")) return new Token(TokenType.CHARACTER, STCharacter.get(readChar()), this, start, stream.getPosition());
        else if (stream.peeks("\'")) return new Token(TokenType.STRING, readStringUntil("\'"), this, start, stream.getPosition());
        else if (stream.peeks("#\'")) return new Token(TokenType.SYMBOL, STSymbol.get(readStringUntil("\'")), this, start, stream.getPosition());
        else if (stream.peeks("#")) return new Token(TokenType.SYMBOL, STSymbol.get(readSymbolLiteral()), this, start, stream.getPosition());
        else if (stream.peeks(":=")) return new Token(TokenType.ASSIGN, this, start, stream.getPosition());
        else if (stream.peeks("(")) return new Token(TokenType.LPAREN, this, start, stream.getPosition());
        else if (stream.peeks(")")) return new Token(TokenType.RPAREN, this, start, stream.getPosition());
        else if (stream.peeks("[")) return new Token(TokenType.LBRACK, this, start, stream.getPosition());
        else if (stream.peeks("]")) return new Token(TokenType.RBRACK, this, start, stream.getPosition());
        else if (stream.peeks("{")) return new Token(TokenType.LCURLY, this, start, stream.getPosition());
        else if (stream.peeks("}")) return new Token(TokenType.RCURLY, this, start, stream.getPosition());
        else if (stream.peeks("|")) return new Token(TokenType.BAR, this, start, stream.getPosition());
        else if (stream.peeks("^")) return new Token(TokenType.CARET, this, start, stream.getPosition());
        else if (stream.peeks(".")) return new Token(TokenType.DOT, this, start, stream.getPosition());
        else if (stream.peeks(",")) return new Token(TokenType.COMMA, this, start, stream.getPosition());
        else if (stream.peeks(":")) return new Token(TokenType.COLON, this, start, stream.getPosition());
        else if (stream.peeks(";")) return new Token(TokenType.SEMICOLON, this, start, stream.getPosition());
        else if (stream.peek() > 0x7f) {
            stream.read();
            return new Token(TokenType.ERROR, this, start, stream.getPosition());
        }

        StringBuilder builder = new StringBuilder();
        while (stream.hasNext()) {
            char c = stream.peek();
            if (isBreakChar(c))
                break;
            builder.append(c);
            stream.read();
        }

        final String str = builder.toString();

        StreamPosition end = stream.getPosition();

        try {
            return new Token(TokenType.INTEGER, Integer.decode(str), this, start, end);
        } catch (NumberFormatException e) {
        }

        if (str.isEmpty()) return new Token(TokenType.EOF, this, start, end);
        else if ("self".equals(str)) return new Token(TokenType.SELF, this, start, end);
        else if ("super".equals(str)) return new Token(TokenType.SUPER, this, start, end);
        else if ("true".equals(str)) return new Token(TokenType.TRUE, this, start, end);
        else if ("false".equals(str)) return new Token(TokenType.FALSE, this, start, end);
        else if ("nil".equals(str)) return new Token(TokenType.NIL, this, start, end);
        else if ("thisContext".equals(str)) return new Token(TokenType.THISCONTEXT, this, start, end);
        else return new Token(TokenType.IDENTIFIER, this.muffleSymbols ? null : STSymbol.get(str), this, start, end);
    }
}
