package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.io.*;

import static nijakow.four.client.editor.GenericTheme.Types.*;

public class GenericTheme extends FTheme {
    public GenericTheme(File file) {
        if (!parseFile(file)) throw new IllegalArgumentException("Could not read file!");
    }

    private boolean parseFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(Types.COMMENT)) {
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
            if (currentLine.startsWith(COMMENT)) {
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
