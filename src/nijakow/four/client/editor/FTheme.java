package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.HashMap;

public abstract class FTheme {
    private final HashMap<TokenType, Style> styles;
    private final Style errorStyle;

    public FTheme() {
        styles = new HashMap<>();
        errorStyle = setErrorStyle();
    }

    protected Style setErrorStyle() {
        StyleContext context = new StyleContext();
        Style ret = context.addStyle(null, null);
        StyleConstants.setUnderline(ret, true);
        StyleConstants.setForeground(ret, Color.red);
        StyleConstants.setBold(ret, true);
        return ret;
    }

    public void addStyle(TokenType type, Style style) {
        styles.put(type, style);
    }

    public Style getStyle(TokenType type) {
        return styles.get(type);
    }

    public Style getErrorStyle() {
        return errorStyle;
    }
}
