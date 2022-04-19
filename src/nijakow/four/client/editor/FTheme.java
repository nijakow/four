package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import java.awt.*;
import java.util.HashMap;

public abstract class FTheme {
    private final HashMap<TokenType, FStyle> styles;
    private final FStyle errorStyle;

    public FTheme() {
        styles = new HashMap<>();
        errorStyle = setErrorStyle();
    }

    protected FStyle setErrorStyle() {
        FStyle ret = new FStyle(null);
        ret.setUnderlined(true);
        ret.setForeground(Color.red);
        ret.setBold(true);
        return ret;
    }

    public void addStyle(TokenType type, FStyle style) {
        styles.put(type, style);
    }

    public FStyle getStyle(TokenType type) {
        return styles.get(type);
    }

    public FStyle getErrorStyle() {
        return errorStyle;
    }
}
