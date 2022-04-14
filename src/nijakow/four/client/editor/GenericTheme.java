package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.share.lang.c.parser.StreamPosition;
import nijakow.four.share.lang.c.parser.StringCharStream;
import nijakow.four.share.lang.c.parser.TokenType;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static nijakow.four.client.editor.GenericTheme.Types.*;

public class GenericTheme extends FTheme {
    private final StringCharStream stream;
    private final List<FStyle> styles;

    public GenericTheme(File file) throws Exception {
        // TODO
        stream = null;
        styles = null;
        if (!parseFile(file)) throw new Exception("Could not read file!");
    }

    public GenericTheme(String content) throws ParseException {
        stream = new StringCharStream("", content);
        styles = new LinkedList<>();
        parseFile();
    }

    private void skipWhitespaces() {
        while (Character.isWhitespace(stream.peek())) stream.advance();
    }

    private FStyle findStyle(TokenType type) {
        for (FStyle style : styles) {
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

    private FStyle parseFStyle() throws ParseException {
        FToken token = nextToken();
        expect(token, FTokenType.IDENTIFIER);
        FStyle fStyle = new FStyle(TokenType.valueOf((String) token.getPayload()));
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

                    case BACKGROUND_NEW:
                        expect(tmp, FTokenType.INT);
                        fStyle.setBackground(new Color((Integer) tmp.getPayload()));
                        break;

                    case FOREGORUND_NEW:
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
                case TYPE: styles.add(parseFStyle()); break;
                default: throw new ParseException(t.getStartPos(), "Expected a style declaration!");
            }
        }
        for (FStyle style : styles) addStyle(style.getTokenType(), style.asStyle(null));
    }

    private String parseComment() {
        StringBuilder builder = new StringBuilder();
        int c;
        while ((c = stream.peek()) != '\n' && c >= 0) {
            builder.append((char) c);
            stream.advance();
        }
        return builder.toString();
    }

    private boolean isSpecial(int c) {
        return !(Character.isAlphabetic(c) || Character.isDigit(c));
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
        while (!isSpecial(stream.peek())) builder.append((char) stream.next());

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

    private boolean parseFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(Types.COMMENT) || line.isEmpty()) {
                    // Ignore, comment.
                } else if (line.contains(SEPARATOR)) {
                    parseStyle(line, reader);
                } else
                    throw new RuntimeException("Error while parsing styles file! Line: " + line);
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private Style parseStyle(String currentLine, final BufferedReader reader) throws IOException {
        TokenType type = TokenType.valueOf(currentLine.substring(0, currentLine.indexOf(SEPARATOR)));
        Style style = new StyleContext().addStyle(null, null);
        boolean first = true;
        while ((currentLine = reader.readLine()) != null) {
            currentLine = currentLine.trim();
            if (currentLine.startsWith(COMMENT) || currentLine.isEmpty()) {
                // Ignore, comment.
            } else {
                int equal = currentLine.indexOf(Types.EQUALS);
                int separator = currentLine.indexOf(SEPARATOR);
                String element, argument;
                if (equal == -1 && separator != -1) {
                    if (first) {
                        style = parseStyle(currentLine, reader);
                    } else {
                        parseStyle(currentLine, reader);
                    }
                } else if (equal == -1 && currentLine.toLowerCase().startsWith(FOREGROUND)) {
                    StyleConstants.setForeground(style, new Color(Integer.parseInt(currentLine.substring(FOREGROUND.length()), 16)));
                } else if (equal == -1 && currentLine.toLowerCase().startsWith(BACKGROUND)) {
                    StyleConstants.setBackground(style, new Color(Integer.parseInt(currentLine.substring(BACKGROUND.length()), 16)));
                } else {
                    element = currentLine.substring(0, equal);
                    argument = currentLine.substring(equal + 1);
                    switch (element.toLowerCase()) {
                        case ALIGNMENT: StyleConstants.setAlignment(style, Integer.parseInt(argument)); break;
                        case BIDI: StyleConstants.setBidiLevel(style, Integer.parseInt(argument)); break;
                        case BOLD: StyleConstants.setBold(style, Boolean.parseBoolean(argument)); break;
                        case FL_INDENT: StyleConstants.setFirstLineIndent(style, Float.parseFloat(argument)); break;
                        case FONT: StyleConstants.setFontFamily(style, argument); break;
                        case ITALIC: StyleConstants.setItalic(style, Boolean.parseBoolean(argument)); break;
                        case SIZE: StyleConstants.setFontSize(style, Integer.parseInt(argument)); break;
                        case STRIKE_THROUGH: StyleConstants.setStrikeThrough(style, Boolean.parseBoolean(argument)); break;
                        case UNDERLINE: StyleConstants.setUnderline(style, Boolean.parseBoolean(argument)); break;

                        default:
                            throw new IllegalStateException("Unexpected value: " + element);
                    }
                }
                first = false;
            }
        }
        addStyle(type, style);
        return style;
    }

    public abstract static class Types {
        public static final String ALIGNMENT      = "alignment";
        public static final String BACKGROUND     = "bg_0x";
        public static final String BACKGROUND_NEW = "background";
        public static final String BIDI           = "bidilevel";
        public static final String BOLD           = "bold";
        public static final String COMMENT        = "#";
        public static final String EQUALS         = "=";
        public static final String FL_INDENT      = "firstline-indent";
        public static final String FONT           = "font";
        public static final String FOREGROUND     = "fg_0x";
        public static final String FOREGORUND_NEW = "foreground";
        public static final String ITALIC         = "italic";
        public static final String SEPARATOR      = ":";
        public static final String SIZE           = "size";
        public static final String STRIKE_THROUGH = "strike-through";
        public static final String UNDERLINE      = "underlined";
    }
}
