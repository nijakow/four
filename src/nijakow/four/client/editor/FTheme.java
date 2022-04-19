package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import java.awt.Color;
import java.util.Collection;
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

    protected Collection<FStyle> getAllStyles() {
        return styles.values();
    }

    public FStyle getStyle(TokenType type) {
        return styles.get(type);
    }

    public FStyle getErrorStyle() {
        return errorStyle;
    }
}
