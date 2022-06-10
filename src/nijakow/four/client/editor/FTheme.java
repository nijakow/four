package nijakow.four.client.editor;

import nijakow.four.smalltalk.parser.TokenType;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;

public abstract class FTheme {
    private final HashMap<TokenType, TokenStyle> styles;
    private final FStyle errorStyle;

    public FTheme() {
        styles = new HashMap<>();
        errorStyle = setErrorStyle();
    }

    protected FStyle setErrorStyle() {
        FStyle ret = new FStyle();
        ret.setUnderlined(true);
        ret.setForeground(Color.red);
        ret.setBold(true);
        return ret;
    }

    public void addStyle(TokenType type, TokenStyle style) {
        styles.put(type, style);
    }

    protected Collection<TokenStyle> getAllStyles() {
        return styles.values();
    }

    public TokenStyle getStyle(TokenType type) {
        return styles.get(type);
    }

    public FStyle getErrorStyle() {
        return errorStyle;
    }
}
