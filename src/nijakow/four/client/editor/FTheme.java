package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.HashMap;

public class FTheme {
    private final HashMap<TokenType, Style> styles;
    private final Style errorStyle;

    public FTheme() {
        styles = new HashMap<>();
        StyleContext context = new StyleContext();
        errorStyle = context.addStyle(null, null);
        StyleConstants.setUnderline(errorStyle, true);
        StyleConstants.setForeground(errorStyle, Color.red);
        StyleConstants.setBold(errorStyle, true);
    }

    public void addStyle(TokenType type, Style style) {
        styles.put(type, style);
    }

    public Style getStyle(TokenType type) {
        return styles.get(type);
    }

    public static FTheme getDefaultTheme(Style defaultStyle) {
        FTheme def = new FTheme();
        StyleContext context = new StyleContext();
        Style s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.orange);
        def.styles.put(TokenType.IDENT, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setItalic(s, true);
        StyleConstants.setForeground(s, Color.blue);
        def.styles.put(TokenType.NIL, s);
        def.styles.put(TokenType.TRUE, s);
        def.styles.put(TokenType.FALSE, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.green);
        def.styles.put(TokenType.INT, s);
        def.styles.put(TokenType.ANY, s);
        def.styles.put(TokenType.VOID, s);
        def.styles.put(TokenType.BOOL, s);
        def.styles.put(TokenType.CHAR, s);
        def.styles.put(TokenType.FUNC, s);
        def.styles.put(TokenType.LIST, s);
        def.styles.put(TokenType.STRING, s);
        def.styles.put(TokenType.OBJECT, s);
        def.styles.put(TokenType.MAPPING, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.red);
        StyleConstants.setBold(s, true);
        def.styles.put(TokenType.IF, s);
        def.styles.put(TokenType.USE, s);
        def.styles.put(TokenType.FOR, s);
        def.styles.put(TokenType.NEW, s);
        def.styles.put(TokenType.THIS, s);
        def.styles.put(TokenType.ELSE, s);
        def.styles.put(TokenType.BREAK, s);
        def.styles.put(TokenType.WHILE, s);
        def.styles.put(TokenType.CLASS, s);
        def.styles.put(TokenType.RETURN, s);
        def.styles.put(TokenType.PUBLIC, s);
        def.styles.put(TokenType.STRUCT, s);
        def.styles.put(TokenType.FOREACH, s);
        def.styles.put(TokenType.PRIVATE, s);
        def.styles.put(TokenType.CONTINUE, s);
        def.styles.put(TokenType.INHERITS, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.blue);
        StyleConstants.setItalic(s, true);
        def.styles.put(TokenType.CONSTANT, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.blue);
        StyleConstants.setItalic(s, true);
        StyleConstants.setBold(s, true);
        def.styles.put(TokenType.C_DOC, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.gray);
        StyleConstants.setItalic(s, true);
        def.styles.put(TokenType.COMMENT, s);
        return def;
    }

    public Style getErrorStyle() {
        return errorStyle;
    }
}
