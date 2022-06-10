package nijakow.four.client.editor;

import nijakow.four.smalltalk.parser.StreamPosition;
import nijakow.four.smalltalk.parser.StringCharacterStream;
import nijakow.four.smalltalk.parser.TokenType;

import java.awt.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static nijakow.four.client.editor.WritableTheme.Types.*;

public class GenericTheme extends WritableTheme {
    private final StringCharacterStream stream;
    private final List<TokenStyle> styles;

    public GenericTheme(File file) throws IOException, ParseException {
        stream = new StringCharacterStream(readFile(file));
        styles = new LinkedList<>();
        parseFile();
    }

    public GenericTheme(String content) throws ParseException {
        stream = new StringCharacterStream(content);
        styles = new LinkedList<>();
        parseFile();
    }

    private String readFile(File file) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int i;
            while ((i = reader.read()) != -1) builder.append((char) i);
        }
        return builder.toString();
    }

    private void skipWhitespaces() {
        while (stream.hasNext() && Character.isWhitespace(stream.peek())) stream.read();
    }

    private TokenStyle findStyle(TokenType type) {
        for (TokenStyle style : styles) {
            if (style.getTokenType() == type) {
                return style;
            }
        }
        return null;
    }

    private void expect(FToken token, FTokenType type) throws ParseException {
        if ((type == FTokenType.TRUE || type == FTokenType.FALSE)) {
            if (!(token.getType() == FTokenType.FALSE || token.getType() == FTokenType.TRUE)) {
                throw new ParseException(token.getStartPos(), "Expected a bool!");
            } else return;
        }
        if (token.getType() != type) {
            String message = "Expected different token!";
            switch (type) {
                case TYPE: message = "Expected 'type'!"; break;
                case COLON: message = "Expected a colon!"; break;
                case L_CURLY: message = "Expected a '{'!"; break;
                case R_CURLY: message = "Expected a '}'"; break;
                case EQUALS: message = "Expected an assignment!"; break;
                case SEMICOLON: message = "Expected a semicolon!"; break;
                case IDENTIFIER: message = "Expected a value!"; break;
                case INT: message = "Expected an integer!"; break;
                case FLOAT: message = "Expected a float!"; break;
            }
            throw new ParseException(token.getStartPos(), message);
        }
    }

    private TokenStyle parseTokenStyle() throws ParseException {
        FToken token = nextToken();
        expect(token, FTokenType.IDENTIFIER);
        TokenStyle fStyle = new TokenStyle(TokenType.valueOf((String) token.getPayload()));
        if ((token = nextToken()).getType() == FTokenType.COLON) {
            FStyle parent = findStyle(TokenType.valueOf((String) (token = nextToken()).getPayload()));
            if (parent == null) throw new ParseException(token.getStartPos(), "Parent not found!");
            fStyle.setParent(parent);
            token = nextToken();
        }
        if (token.getType() == FTokenType.L_CURLY) {
            while ((token = nextToken()).getType() != FTokenType.R_CURLY) {
                if (token.getType() == FTokenType.COMMENT || token.getType() == FTokenType.SEMICOLON) continue;
                expect(token, FTokenType.IDENTIFIER);
                FToken tmp = nextToken();
                expect(tmp, FTokenType.EQUALS);
                tmp = nextToken();
                switch ((String) token.getPayload()) {
                    case ALIGNMENT:
                        expect(tmp, FTokenType.INT);
                        fStyle.setAlignment((Integer) tmp.getPayload());
                        break;

                    case BIDI:
                        expect(tmp, FTokenType.INT);
                        fStyle.setBidiLevel((Integer) tmp.getPayload());
                        break;

                    case SIZE:
                        expect(tmp, FTokenType.INT);
                        fStyle.setSize((Integer) tmp.getPayload());
                        break;

                    case FL_INDENT:
                        expect(tmp, FTokenType.FLOAT);
                        fStyle.setFirstLineIndent((Float) tmp.getPayload());
                        break;

                    case FONT:
                        expect(tmp, FTokenType.IDENTIFIER);
                        fStyle.setFamily((String) tmp.getPayload());
                        break;

                    case BOLD:
                        expect(tmp, FTokenType.TRUE);
                        fStyle.setBold(tmp.getType() == FTokenType.TRUE);
                        break;

                    case ITALIC:
                        expect(tmp, FTokenType.TRUE);
                        fStyle.setItalic(tmp.getType() == FTokenType.TRUE);
                        break;

                    case STRIKE_THROUGH:
                        expect(tmp, FTokenType.TRUE);
                        fStyle.setStrikeThrough(tmp.getType() == FTokenType.TRUE);
                        break;

                    case UNDERLINE:
                        expect(tmp, FTokenType.TRUE);
                        fStyle.setUnderlined(tmp.getType() == FTokenType.TRUE);
                        break;

                    case BACKGROUND:
                        expect(tmp, FTokenType.INT);
                        fStyle.setBackground(new Color((Integer) tmp.getPayload()));
                        break;

                    case FOREGROUND:
                        expect(tmp, FTokenType.INT);
                        fStyle.setForeground(new Color((Integer) tmp.getPayload()));
                        break;

                    default: throw new ParseException(tmp.getStartPos(), "Expected a value!");
                }
            }
        } else throw new ParseException(token.getStartPos(), "Expected style definition!");
        return fStyle;
    }

    private void parseFile() throws ParseException {
        FToken t;
        while ((t = nextToken()).getType() != FTokenType.EOF) {
            switch (t.getType()) {
                case COMMENT: continue;
                case TYPE: styles.add(parseTokenStyle()); break;
                default: throw new ParseException(t.getStartPos(), "Expected a style declaration!");
            }
        }
        for (TokenStyle style : styles) addStyle(style.getTokenType(), style);
    }

    private String parseComment() {
        StringBuilder builder = new StringBuilder();
        int c;
        while ((c = stream.peek()) != '\n') {
            builder.append((char) c);
            stream.read();
        }
        return builder.toString();
    }

    private boolean isSpecial(int c) {
        return !(Character.isAlphabetic(c) || Character.isDigit(c) || c == '_' || c == '-');
    }

    private FToken nextToken() {
        skipWhitespaces();

        final StreamPosition pos = stream.getPosition();

        if (stream.peeks("#")) return new FToken(FTokenType.COMMENT, pos, parseComment(), stream.getPosition());
        else if (stream.peeks(":")) return new FToken(FTokenType.COLON, pos, null, stream.getPosition());
        else if (stream.peeks("{")) return new FToken(FTokenType.L_CURLY, pos, null, stream.getPosition());
        else if (stream.peeks("}")) return new FToken(FTokenType.R_CURLY, pos, null, stream.getPosition());
        else if (stream.peeks("=")) return new FToken(FTokenType.EQUALS, pos, null, stream.getPosition());
        else if (stream.peeks(";")) return new FToken(FTokenType.SEMICOLON, pos, null, stream.getPosition());

        final StringBuilder builder = new StringBuilder();
        while (stream.hasNext() && !isSpecial(stream.peek())) builder.append(stream.read());

        final String raw = builder.toString();

        try {
            return new FToken(FTokenType.INT, pos, Integer.decode(raw), stream.getPosition());
        } catch (NumberFormatException e) {}

        switch (raw) {
            case "": return new FToken(FTokenType.EOF, pos, null, stream.getPosition());
            case "type": return new FToken(FTokenType.TYPE, pos, null, stream.getPosition());
            case "true": return new FToken(FTokenType.TRUE, pos, null, stream.getPosition());
            case "false": return new FToken(FTokenType.FALSE, pos, null, stream.getPosition());
            default: return new FToken(FTokenType.IDENTIFIER, pos, raw, stream.getPosition());
        }
    }

    public static class ParseException extends Exception {
        private final StreamPosition position;
        private final String errorText;

        public ParseException(StreamPosition position, String message) {
            super(message);
            this.position = position;
            this.errorText = position == null ? message : position.makeErrorText(message);
        }

        public String getErrorText() {
            return errorText;
        }

        public StreamPosition getPosition() {
            return position;
        }
    }
}
