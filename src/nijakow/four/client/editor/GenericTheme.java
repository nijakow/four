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
import java.util.Stack;

import static nijakow.four.client.editor.GenericTheme.Types.*;

public class GenericTheme extends FTheme {
    private final StringCharStream stream;

    public GenericTheme(File file) throws Exception {
        // TODO
        stream = null;
        if (!parseFile(file)) throw new Exception("Could not read file!");
    }

    public GenericTheme(String content) throws ParseException {
        stream = new StringCharStream("", content);
        parseFile();
    }

    private void skipWhitespaces() {
        while (Character.isWhitespace(stream.peek())) stream.advance();
    }

    private void parseFile() throws ParseException {
        // TODO
        FToken t;
        while ((t = nextToken()).getType() != FTokenType.EOF) {
            System.out.println(t.getType() + ": " + t.getPayload());
        }
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
        public static final String BIDI           = "bidilevel";
        public static final String BOLD           = "bold";
        public static final String COMMENT        = "#";
        public static final String EQUALS         = "=";
        public static final String FL_INDENT      = "firstline-indent";
        public static final String FONT           = "font";
        public static final String FOREGROUND     = "fg_0x";
        public static final String ITALIC         = "italic";
        public static final String SEPARATOR      = ":";
        public static final String SIZE           = "size";
        public static final String STRIKE_THROUGH = "stroke-through";
        public static final String UNDERLINE      = "underline";
    }
}
