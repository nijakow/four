package nijakow.four.client.editor;

import nijakow.four.client.utils.StringHelper;
import nijakow.four.share.lang.c.parser.*;

import javax.swing.text.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FDocument extends DefaultStyledDocument {
    private final Style def;
    private boolean highlighting;
    private boolean autoIndenting;
    private FTheme theme;
    private final ExecutorService threads;

    public FDocument() {
        threads = Executors.newSingleThreadScheduledExecutor();
        def = getLogicalStyle(0);
        theme = new DefaultTheme();
        highlighting = false;
    }

    public FDocument(boolean highlighting) {
        this();
        this.highlighting = highlighting;
    }

    public void setTheme(FTheme theme) {
        if (theme == null) {
            this.theme = new DefaultTheme();
        } else {
            this.theme = theme;
        }
    }

    public FTheme getTheme() { return theme; }

    public void setAutoIndentingEnabled(boolean enabled) {
        autoIndenting = enabled;
    }

    public boolean getAutoIndentingEnabled() {
        return autoIndenting;
    }

    public void setHighlightingEnabled(boolean highlighting) {
        this.highlighting = highlighting;
        if (!highlighting)
            resetHighlight();
        else
            threads.execute(this::updateSyntaxHighlighting);
    }

    public boolean isSyntaxHighlighting() {
        return highlighting;
    }

    private String getLineIndent(int offs) throws BadLocationException {
        final String line = getText(0, getLength()).substring(getLineStart(offs), getLineEnd(offs));
        int i;
        for (i = 0; i < line.length() && Character.isWhitespace(line.charAt(i)); i++);
        return StringHelper.generateFilledString(' ', i);
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        final String text = getText(0, getLength());
        if (autoIndenting) {
            if (str.equals("\n")) {
                str += getLineIndent(offs);
                if (offs > 0 && text.charAt(offs - 1) == '{') {
                    str += "    ";
                }
            } else if (str.equals("}") && isOnlyWhitespacesOnLine(offs)) {
                final int indent = Math.min(Math.min(getLineIndent(offs).length(), 4), Math.max(0, offs - getLineStart(offs)));
                super.remove(offs - indent, indent);
                offs -= indent;
            }
        }
        if (str.equals("\t")) {
            str = "    ";
        }
        super.insertString(offs, str, a);
        if (highlighting) {
            String finalStr = str;
            int finalOffs = offs;
            threads.execute(() -> updateSyntaxHighlighting2(finalOffs, finalStr.length(), text));
        }
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {
        int lineStart = getLineStart(offs);
        int lineEnd = getLineEnd(offs);
        if (len == 1 && isOnlyWhitespacesOnLine(lineEnd)) {
            lineStart = lineStart == 0 ? 0 : lineStart - 1;
            lineEnd = lineEnd == lineStart ? lineEnd + 1 : lineEnd;
            len = lineEnd - lineStart;
            offs = lineStart;
        }
        final String oldText = getText(0, getLength());
        super.remove(offs, len);
        if (highlighting) {
            int finalOffs = offs;
            threads.execute(() -> updateSyntaxHighlighting2(finalOffs, 0, oldText));
        }
    }

    public void resetHighlight() {
        setCharacterAttributes(0, getLength(), def, true);
    }

    public List<FSuggestion> computeSuggestions(int cursorPosition) {
        List<FSuggestion> ret = new ArrayList<>();
        ret.add(new FSuggestion("Not implemented yet!"));
        return ret;
    }

    public int getLineEnd(int position) {
        String text;
        try {
            text = getText(0, getLength());
        } catch (BadLocationException e) {
            return -1;
        }
        int i;
        for (i = position; i < text.length() && text.charAt(i) != '\n'; i++);
        return i;
    }

    public int getLineStart(int position) {
        String text;
        try {
            text = getText(0, getLength());
        } catch (BadLocationException e) {
            return -1;
        }
        int i;
        for (i = position - 1; i >= 0 && text.charAt(i) != '\n'; i--);
        return i + 1;
    }

    private boolean isOnlyWhitespacesOnLine(int endPosition) {
        String text;
        try {
            text = getText(0, getLength());
        } catch (BadLocationException e) {
            return false;
        }
        for (int start = getLineStart(endPosition), i = endPosition - 1; i >= start; i--) {
            if (!Character.isWhitespace(text.charAt(i))) return false;
        }
        return true;
    }

    public void updateSyntaxHighlighting() {
        String text;
        try {
            text = getText(0, getLength());
        } catch (BadLocationException e) {
            return;
        }
        updateSyntaxHighlighting2(0, getLength(), text);
    }

    private boolean isInsideComment(int index, final String text) {
        return isInsideBlockComment(index, text) || isInsideDocComment(index, text);
    }

    private boolean isInsideComment(int index) throws BadLocationException {
        return isInsideComment(index, getText(0, getLength()));
    }

    private boolean isInsideDocComment(int index) throws BadLocationException {
        return isInsideDocComment(index, getText(0, getLength()));
    }

    private boolean isInsideDocComment(int index, final String text) {
        final String before = text.substring(0, index);
        final int lastClose = before.lastIndexOf("*/");
        final int lastOpen = before.lastIndexOf("/**");
        if (lastClose == -1 && lastOpen != -1) {
            return true;
        }
        else if (lastClose != -1 && lastOpen != -1) {
            return lastOpen > lastClose;
        }
        return false;
    }

    private boolean isInsideBlockComment(int index) throws BadLocationException {
        return isInsideBlockComment(index, getText(0, getLength()));
    }

    private boolean isInsideBlockComment(int index, final String text) {
        final String before = text.substring(0, index);
        final int lastClose = before.lastIndexOf("*/");
        final int lastOpen = before.lastIndexOf("/*");
        if (lastClose == -1 && lastOpen != -1) {
            return true;
        }
        else if (lastClose != -1 && lastOpen != -1) {
            return lastOpen > lastClose;
        }
        return false;
    }

    private void updateSyntaxHighlighting2(int offset, int length, final String oldText) {
        try {
            final String text = getText(0, getLength());
            int lineStart = getLineStart(offset);
            int lineEnd = getLineEnd(offset + length);
            final int oldLineEnd = oldText.length() - text.length() + lineEnd;
            final boolean first = isInsideComment(lineStart);
            final boolean second = isInsideComment(lineEnd);
            if (first) {
                lineStart = text.substring(0, lineStart).lastIndexOf("/*");
            }
            if (second) {
                final int bce = text.indexOf("*/", lineEnd);
                lineEnd = bce == -1 ? text.length() : bce + 2;
            } else if (first || (oldLineEnd > 0 && oldLineEnd < oldText.length() && isInsideComment(oldLineEnd, oldText))) {
                final int bco = text.indexOf("*/", lineEnd);
                lineEnd = bco == -1 ? text.length() : bco + 2;
            }
            String line = text.substring(lineStart, lineEnd);
            Tokenizer tokenizer = new Tokenizer(new StringCharStream("", line));
            tokenizer.enableCommentTokens();
            Token token;
            do {
                token = tokenizer.nextToken();
                Style style = theme.getStyle(token.getType()) == null ? def : theme.getStyle(token.getType()).asStyle(def);
                if (style == null) style = def;
                int pos = token.getPosition().getIndex() + lineStart;
                setCharacterAttributes(pos, (token.getEndPosition().getIndex() + lineStart) - pos, style, true);
            } while (token.getType() != TokenType.EOF);
        } catch (Exception e) {
            // TODO Handle this gracefully
            e.printStackTrace();
        }
    }
}
