package nijakow.four.client.editor;

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

    public FStyle() {
        this(null);
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
    }

    public FStyle getParent() {
        return parent;
    }

    public void setParent(FStyle parent) {
        this.parent = parent;
    }

    public Boolean isBold() {
        return parent != null && bold == null ? parent.isBold() : bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public Boolean isItalic() {
        return parent != null && italic == null ? parent.isItalic() : italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
    }

    public Boolean isStrikeThrough() {
        return parent != null && strike == null ? parent.isStrikeThrough() : strike;
    }

    public void setStrikeThrough(boolean strike) {
        this.strike = strike;
    }

    public Boolean isUnderlined() {
        return parent != null && underlined == null ? parent.isUnderlined() : underlined;
    }

    public void setUnderlined(boolean underlined) {
        this.underlined = underlined;
    }

    public Integer getAlignment() {
        return parent != null && alignment == null ? parent.getAlignment() : alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public Integer getBidiLevel() {
        return parent != null && bidiLevel == null ? parent.getBidiLevel() : bidiLevel;
    }

    public void setBidiLevel(int bidiLevel) {
        this.bidiLevel = bidiLevel;
    }

    public Integer getSize() {
        return parent != null && size == null ? parent.getSize() : size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Float getFirstLineIndent() {
        return parent != null && firstLineIndent == null ? parent.getFirstLineIndent() : firstLineIndent;
    }

    public void setFirstLineIndent(float firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    public String getFamily() {
        return parent != null && family == null ? parent.getFamily() : family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public Color getBackground() {
        return parent != null && background == null ? parent.getBackground() : background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public Color getForeground() {
        return parent != null && foreground == null ? parent.getForeground() : foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
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
}
