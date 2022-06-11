package nijakow.four.client.editor;

import nijakow.four.client.utils.StringHelper;
import nijakow.four.smalltalk.parser.StringCharacterStream;
import nijakow.four.smalltalk.parser.Token;
import nijakow.four.smalltalk.parser.TokenType;
import nijakow.four.smalltalk.parser.Tokenizer;

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

    private boolean isIndentIncChar(char c) {
        return c == '[' || c == '{';
    }

    private boolean isIndentDecStr(String str) {
        return "]".equals(str) || "}".equals(str);
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        final String text = getText(0, getLength());
        if (autoIndenting) {
            if (str.equals("\n")) {
                str += getLineIndent(offs);
                if (offs > 0 && isIndentIncChar(text.charAt(offs - 1))) {
                    str += "    ";
                }
            } else if (isIndentDecStr(str) && isOnlyWhitespacesOnLine(offs)) {
                final int indent = Math.min(Math.min(getLineIndent(offs).length(), 4), Math.max(0, offs - getLineStart(offs)));
                super.remove(offs - indent, indent);
                offs -= indent;
            }
        }
        if (str.equals("\t")) { // TODO Replace all tabs by spaces
            str = "    ";
        }
        super.insertString(offs, str, a);
        if (highlighting) {
            threads.execute(this::updateSyntaxHighlighting);
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
        super.remove(offs, len);
        if (highlighting) {
            threads.execute(this::updateSyntaxHighlighting);
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
        Token token = null;
        int pos = 0;
        try {
            final String text = getText(0, getLength());
            Tokenizer tokenizer = new Tokenizer(new StringCharacterStream(text));
            tokenizer.enableCommentTokens();
            tokenizer.muffleSymbols();
            do {
                token = tokenizer.nextToken();
                Style style = theme.getStyle(token.getType()) == null ? def : theme.getStyle(token.getType()).asStyle(def);
                if (style == null) style = def;
                pos = token.getPosition().getIndex();
                setCharacterAttributes(pos, (token.getEndPosition().getIndex()) - pos, style, true);
            } while (token.getType() != TokenType.EOF);
        } catch (Exception e) {
            // TODO Handle this gracefully
            System.err.println();
            System.err.println("-------------------");
            if (token != null) {
                System.err.println("Token type: " + token.getType());
                System.err.println("Token start: " + token.getPosition().getIndex());
                System.err.println("Token end: " + token.getEndPosition().getIndex());
            }
            System.err.println("Pos: " + pos);
            try {
                System.err.println("Full text: '" + getText(0, getLength()) + "'");
            } catch (BadLocationException ex) {
                throw new RuntimeException(ex);
            }
            System.err.println("Full length: " + getLength());
            System.err.println();
            e.printStackTrace();
            System.err.println("-------------------");
        }
    }
}
