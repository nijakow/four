package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.Color;

public class FStyle {
    private Boolean bold;
    private Boolean italic;
    private Boolean strike;
    private Boolean underlined;
    private Integer alignment;
    private Integer bidiLevel;
    private Integer size;
    private Float firstLineIndent;
    private String family;
    private Color background;
    private Color foreground;
    private FStyle parent;
    private TokenType tokenType;

    public FStyle() {
        this((TokenType) null);
    }

    public FStyle(Style style) {
        this();
        setForeground(StyleConstants.getForeground(style));
        setBackground(StyleConstants.getBackground(style));
        setBold(StyleConstants.isBold(style));
        setItalic(StyleConstants.isItalic(style));
        setStrikeThrough(StyleConstants.isStrikeThrough(style));
        setUnderlined(StyleConstants.isUnderline(style));
        setAlignment(StyleConstants.getAlignment(style));
        setBidiLevel(StyleConstants.getBidiLevel(style));
        setSize(StyleConstants.getFontSize(style));
        setFirstLineIndent(StyleConstants.getFirstLineIndent(style));
        setFamily(StyleConstants.getFontFamily(style));
    }

    public FStyle(FStyle original, boolean resolveInheritance) {
        if (resolveInheritance) {
            setParent(null);
            bold = original.isBold();
            italic = original.isItalic();
            strike = original.isStrikeThrough();
            underlined = original.isUnderlined();
            alignment = original.getAlignment();
            bidiLevel = original.getBidiLevel();
            size = original.getSize();
            firstLineIndent = original.getFirstLineIndent();
            family = original.getFamily();
            background = original.getBackground();
            foreground = original.getForeground();
        } else {
            setParent(original.getParent());
            bold = original.bold;
            italic = original.italic;
            strike = original.strike;
            underlined = original.underlined;
            alignment = original.alignment;
            bidiLevel = original.bidiLevel;
            size = original.size;
            firstLineIndent = original.firstLineIndent;
            family = original.family;
            background = original.background;
            foreground = original.foreground;
        }
        setTokenType(original.getTokenType());
    }

    public FStyle(TokenType tokenType) {
        this(tokenType, null);
    }

    public FStyle(TokenType tokenType, FStyle parent) {
        this.tokenType = tokenType;
        setParent(parent);
        bold = null;
        italic = null;
        strike = null;
        underlined = null;
        alignment = null;
        bidiLevel = null;
        size = null;
        firstLineIndent = null;
        family = null;
        background = null;
        foreground = null;
    }

    public FStyle getParent() {
        return parent != null && parent.getTokenType() == null && parent.getParent() == null ? null : parent;
    }

    public void setParent(FStyle parent) {
        this.parent = parent;
    }

    public Boolean isBold() {
        return parent != null && bold == null ? parent.isBold() : bold;
    }

    public void setBold(Boolean bold) {
        this.bold = bold;
    }

    public boolean isBoldOverwritten() {
        return bold != null;
    }

    public Boolean isItalic() {
        return parent != null && italic == null ? parent.isItalic() : italic;
    }

    public void setItalic(Boolean italic) {
        this.italic = italic;
    }

    public boolean isItalicOverwritten() {
        return italic != null;
    }

    public Boolean isStrikeThrough() {
        return parent != null && strike == null ? parent.isStrikeThrough() : strike;
    }

    public void setStrikeThrough(Boolean strike) {
        this.strike = strike;
    }

    public boolean isStrikeThroughOverwritten() {
        return strike != null;
    }

    public Boolean isUnderlined() {
        return parent != null && underlined == null ? parent.isUnderlined() : underlined;
    }

    public void setUnderlined(Boolean underlined) {
        this.underlined = underlined;
    }

    public boolean isUnderlinedOverwritten() {
        return underlined != null;
    }

    public Integer getAlignment() {
        return parent != null && alignment == null ? parent.getAlignment() : alignment;
    }

    public void setAlignment(Integer alignment) {
        this.alignment = alignment;
    }

    public boolean isAlignmentOverwritten() {
        return alignment != null;
    }

    public Integer getBidiLevel() {
        return parent != null && bidiLevel == null ? parent.getBidiLevel() : bidiLevel;
    }

    public void setBidiLevel(Integer bidiLevel) {
        this.bidiLevel = bidiLevel;
    }

    public boolean isBidiLevelOverwritten() {
        return bidiLevel != null;
    }

    public Integer getSize() {
        return parent != null && size == null ? parent.getSize() : size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean isSizeOverwritten() {
        return size != null;
    }

    public Float getFirstLineIndent() {
        return parent != null && firstLineIndent == null ? parent.getFirstLineIndent() : firstLineIndent;
    }

    public void setFirstLineIndent(Float firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    public boolean isFirstLineIndentOverwritten() {
        return firstLineIndent != null;
    }

    public String getFamily() {
        return parent != null && family == null ? parent.getFamily() : family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public boolean isFamilyOverwritten() {
        return family != null;
    }

    public Color getBackground() {
        return parent != null && background == null ? parent.getBackground() : background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public boolean isBackgroundOverwritten() {
        return background != null;
    }

    public Color getForeground() {
        return parent != null && foreground == null ? parent.getForeground() : foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public boolean isForegroundOverwritten() {
        return foreground != null;
    }

    public Style asStyle(Style parent) {
        Style ret = StyleContext.getDefaultStyleContext().addStyle(null, parent);
        if (isBold() != null) StyleConstants.setBold(ret, isBold());
        if (isItalic() != null) StyleConstants.setItalic(ret, isItalic());
        if (isUnderlined() != null) StyleConstants.setUnderline(ret, isUnderlined());
        if (isStrikeThrough() != null) StyleConstants.setStrikeThrough(ret, isStrikeThrough());
        if (getAlignment() != null) StyleConstants.setAlignment(ret, getAlignment());
        if (getBidiLevel() != null) StyleConstants.setBidiLevel(ret, getBidiLevel());
        if (getSize() != null) StyleConstants.setFontSize(ret, getSize());
        if (getFirstLineIndent() != null) StyleConstants.setFirstLineIndent(ret, getFirstLineIndent());
        if (getBackground() != null) StyleConstants.setBackground(ret, getBackground());
        if (getForeground() != null) StyleConstants.setForeground(ret, getForeground());
        if (getFamily() != null) StyleConstants.setFontFamily(ret, getFamily());
        return ret;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }
}
