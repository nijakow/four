package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;

public abstract class FTheme {
    private final HashMap<nijakow.four.smalltalk.parser.TokenType, FStyle> styles;
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

    public void addStyle(nijakow.four.smalltalk.parser.TokenType type, FStyle style) {
        styles.put(type, style);
    }

    protected Collection<FStyle> getAllStyles() {
        return styles.values();
    }

    public FStyle getStyle(nijakow.four.smalltalk.parser.TokenType type) {
        return styles.get(type);
    }

    public FStyle getErrorStyle() {
        return errorStyle;
    }
}
