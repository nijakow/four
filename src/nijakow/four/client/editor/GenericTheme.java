package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.share.lang.c.parser.StreamPosition;
import nijakow.four.share.lang.c.parser.StringCharStream;
import nijakow.four.share.lang.c.parser.TokenType;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import static nijakow.four.client.editor.GenericTheme.Types.*;

public class GenericTheme extends FTheme {
    private final StringCharStream stream;
    private final List<FStyle> styles;

    public GenericTheme(File file) throws IOException, ParseException {
        stream = new StringCharStream(file.getName(), readFile(file));
        styles = new LinkedList<>();
        parseFile();
    }

    public GenericTheme(String content) throws ParseException {
        stream = new StringCharStream("", content);
        styles = new LinkedList<>();
        parseFile();
    }

    private String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder builder = new StringBuilder();
        int i;
        while ((i = reader.read()) != -1) builder.append((char) i);
        return builder.toString();
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
                case TYPE: styles.add(parseFStyle()); break;
                default: throw new ParseException(t.getStartPos(), "Expected a style declaration!");
            }
        }
        for (FStyle style : styles) addStyle(style.getTokenType(), style);
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

    public String generateText() {
        StringBuilder builder = new StringBuilder();
        for (FStyle style : getAllStyles()) {
            builder.append("type ");
            builder.append(style.getTokenType());
            if (style.getParent() != null) builder.append(" : ").append(style.getParent().getTokenType());
            builder.append(" {\n");
            if (style.isBoldOverwritten()) {
                builder.append("    ")
                        .append(BOLD)
                        .append(" = ")
                        .append(style.isBold())
                        .append('\n');
            }
            if (style.isItalicOverwritten()) {
                builder.append("    ")
                        .append(ITALIC)
                        .append(" = ")
                        .append(style.isItalic())
                        .append('\n');
            }
            if (style.isUnderlinedOverwritten()) {
                builder.append("    ")
                        .append(UNDERLINE)
                        .append(" = ")
                        .append(style.isUnderlined())
                        .append('\n');
            }
            if (style.isStrikeThroughOverwritten()) {
                builder.append("    ")
                        .append(STRIKE_THROUGH)
                        .append(" = ")
                        .append(style.isStrikeThrough())
                        .append('\n');
            }
            if (style.isAlignmentOverwritten()) {
                builder.append("    ")
                        .append(ALIGNMENT)
                        .append(" = ")
                        .append(style.getAlignment())
                        .append('\n');
            }
            if (style.isBidiLevelOverwritten()) {
                builder.append("    ")
                        .append(BIDI)
                        .append(" = ")
                        .append(style.getBidiLevel())
                        .append('\n');
            }
            if (style.isForegroundOverwritten()) {
                builder.append("    ")
                        .append(FOREGROUND)
                        .append(" = ")
                        .append(Integer.toString(style.getForeground().getRGB(), 16))
                        .append('\n');
            }
            if (style.isBackgroundOverwritten()) {
                builder.append("    ")
                        .append(BACKGROUND)
                        .append(" = ")
                        .append(Integer.toString(style.getBackground().getRGB(), 16))
                        .append('\n');
            }
            if (style.isSizeOverwritten()) {
                builder.append("    ")
                        .append(SIZE).
                        append(" = ").
                        append(style.getSize()).
                        append('\n');
            }
            if (style.isFirstLineIndentOverwritten()) {
                builder.append("    ")
                        .append(FL_INDENT)
                        .append(" = ")
                        .append(style.getFirstLineIndent())
                        .append('\n');
            }
            if (style.isFamilyOverwritten()) {
                builder.append("    ")
                        .append(FONT)
                        .append(" = ")
                        .append(style.getFamily())
                        .append('\n');
            }
            builder.append("}\n\n");
        }
        return builder.toString();
    }

    public void saveToFile(File file) throws IOException {
        BufferedOutputStream os = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
        os.write(generateText().getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    public abstract static class Types {
        public static final String ALIGNMENT      = "alignment";
        public static final String BACKGROUND     = "background";
        public static final String BIDI           = "bidilevel";
        public static final String BOLD           = "bold";
        public static final String FL_INDENT      = "firstline-indent";
        public static final String FONT           = "font";
        public static final String FOREGROUND     = "foreground";
        public static final String ITALIC         = "italic";
        public static final String SIZE           = "size";
        public static final String STRIKE_THROUGH = "strike-through";
        public static final String UNDERLINE      = "underlined";
    }
}
