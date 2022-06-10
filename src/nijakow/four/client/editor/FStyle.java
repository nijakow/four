package nijakow.four.client.editor;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.Color;
import java.util.Objects;

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
    private Style cached;

    public FStyle() {
        this((FStyle) null);
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
        cached = null;
    }

    public FStyle(FStyle parent) {
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
        cached = null;
    }

    public FStyle getParent() {
        return parent;
    }

    private boolean inherits(FStyle other) {
        if (getParent() == null) return false;
        if (getParent() == other) return true;
        return getParent().inherits(other);
    }

    public void setParent(FStyle parent) {
        final FStyle oldParent = this.parent;
        this.parent = parent;
        if (inherits(this)) {
            this.parent = oldParent;
            throw new IllegalArgumentException("A FStyle must not inherit itself!");
        }
        cached = null;
    }

    public Boolean isBold() {
        return parent != null && bold == null ? parent.isBold() : bold;
    }

    public void setBold(Boolean bold) {
        if (!Objects.equals(this.bold, bold)) {
            this.bold = bold;
            cached = null;
        }
    }

    public boolean isBoldOverwritten() {
        return bold != null;
    }

    public Boolean isItalic() {
        return parent != null && italic == null ? parent.isItalic() : italic;
    }

    public void setItalic(Boolean italic) {
        if (!Objects.equals(this.italic, italic)) {
            this.italic = italic;
            cached = null;
        }
    }

    public boolean isItalicOverwritten() {
        return italic != null;
    }

    public Boolean isStrikeThrough() {
        return parent != null && strike == null ? parent.isStrikeThrough() : strike;
    }

    public void setStrikeThrough(Boolean strike) {
        if (!Objects.equals(this.strike, strike)) {
            this.strike = strike;
            cached = null;
        }
    }

    public boolean isStrikeThroughOverwritten() {
        return strike != null;
    }

    public Boolean isUnderlined() {
        return parent != null && underlined == null ? parent.isUnderlined() : underlined;
    }

    public void setUnderlined(Boolean underlined) {
        if (!Objects.equals(this.underlined, underlined)) {
            this.underlined = underlined;
            cached = null;
        }
    }

    public boolean isUnderlinedOverwritten() {
        return underlined != null;
    }

    public Integer getAlignment() {
        return parent != null && alignment == null ? parent.getAlignment() : alignment;
    }

    public void setAlignment(Integer alignment) {
        if (!Objects.equals(this.alignment, alignment)) {
            this.alignment = alignment;
            cached = null;
        }
    }

    public boolean isAlignmentOverwritten() {
        return alignment != null;
    }

    public Integer getBidiLevel() {
        return parent != null && bidiLevel == null ? parent.getBidiLevel() : bidiLevel;
    }

    public void setBidiLevel(Integer bidiLevel) {
        if (!Objects.equals(this.bidiLevel, bidiLevel)) {
            this.bidiLevel = bidiLevel;
            cached = null;
        }
    }

    public boolean isBidiLevelOverwritten() {
        return bidiLevel != null;
    }

    public Integer getSize() {
        return parent != null && size == null ? parent.getSize() : size;
    }

    public void setSize(Integer size) {
        if (!Objects.equals(this.size, size)) {
            this.size = size;
            cached = null;
        }
    }

    public boolean isSizeOverwritten() {
        return size != null;
    }

    public Float getFirstLineIndent() {
        return parent != null && firstLineIndent == null ? parent.getFirstLineIndent() : firstLineIndent;
    }

    public void setFirstLineIndent(Float firstLineIndent) {
        if (!Objects.equals(this.firstLineIndent, firstLineIndent)) {
            this.firstLineIndent = firstLineIndent;
            cached = null;
        }
    }

    public boolean isFirstLineIndentOverwritten() {
        return firstLineIndent != null;
    }

    public String getFamily() {
        return parent != null && family == null ? parent.getFamily() : family;
    }

    public void setFamily(String family) {
        if (!Objects.equals(this.family, family)) {
            this.family = family;
            cached = null;
        }
    }

    public boolean isFamilyOverwritten() {
        return family != null;
    }

    public Color getBackground() {
        return parent != null && background == null ? parent.getBackground() : background;
    }

    public void setBackground(Color background) {
        if (!Objects.equals(background, this.background)) {
            this.background = background;
            cached = null;
        }
    }

    public boolean isBackgroundOverwritten() {
        return background != null;
    }

    public Color getForeground() {
        return parent != null && foreground == null ? parent.getForeground() : foreground;
    }

    public void setForeground(Color foreground) {
        if (!Objects.equals(foreground, this.foreground)) {
            this.foreground = foreground;
            cached = null;
        }
    }

    public boolean isForegroundOverwritten() {
        return foreground != null;
    }

    public Style asStyle(Style parent) {
        if (cached == null) {
            cached = StyleContext.getDefaultStyleContext().addStyle(null, parent);
            if (isBold() != null) StyleConstants.setBold(cached, isBold());
            if (isItalic() != null) StyleConstants.setItalic(cached, isItalic());
            if (isUnderlined() != null) StyleConstants.setUnderline(cached, isUnderlined());
            if (isStrikeThrough() != null) StyleConstants.setStrikeThrough(cached, isStrikeThrough());
            if (getAlignment() != null) StyleConstants.setAlignment(cached, getAlignment());
            if (getBidiLevel() != null) StyleConstants.setBidiLevel(cached, getBidiLevel());
            if (getSize() != null) StyleConstants.setFontSize(cached, getSize());
            if (getFirstLineIndent() != null) StyleConstants.setFirstLineIndent(cached, getFirstLineIndent());
            if (getBackground() != null) StyleConstants.setBackground(cached, getBackground());
            if (getForeground() != null) StyleConstants.setForeground(cached, getForeground());
            if (getFamily() != null) StyleConstants.setFontFamily(cached, getFamily());
        }
        return cached;
    }

    protected boolean hasCached() {
        return cached != null;
    }

    protected void invalidateCache() {
        cached = null;
    }
}
