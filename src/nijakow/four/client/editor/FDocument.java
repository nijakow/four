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
    private String text;
    private boolean highlighting;
    private boolean autoIndenting;
    private FTheme theme;
    private final ExecutorService threads;

    public FDocument() {
        threads = Executors.newSingleThreadScheduledExecutor();
        def = getLogicalStyle(0);
        theme = new DefaultTheme();
//        idents = new ArrayList<>();
//        ide = new ArrayList<>();
        highlighting = false;
        try {
            text = getText(0, getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
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
            threads.execute(() -> updateSyntaxHighlighting2(0, getLength()));
    }

    public boolean isSyntaxHighlighting() {
        return highlighting;
    }

    @Override
    protected void postRemoveUpdate(DefaultDocumentEvent chng) {
        super.postRemoveUpdate(chng);
        try {
            text = getText(0, getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private String getLineIndent(int offs) {
        final String line = text.substring(getLineStart(offs), getLineEnd(offs));
        int i;
        for (i = 0; i < line.length() && Character.isWhitespace(line.charAt(i)); i++);
        return StringHelper.generateFilledString(' ', i);
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (autoIndenting) {
            if (str.equals("\n")) {
                str += getLineIndent(offs);
                if (offs > 0 && text.charAt(offs - 1) == '{') {
                    str += "    ";
                }
            } else if (str.equals("}") && isOnlyWhitespacesOnLine(offs)) {
                final int indent = Math.min(getLineIndent(offs).length(), 4);
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
            threads.execute(() -> updateSyntaxHighlighting2(finalOffs, finalStr.length()));
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
            int finalOffs = offs;
            threads.execute(() -> updateSyntaxHighlighting2(finalOffs, 0));
        }
    }

    @Override
    protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
        super.insertUpdate(chng, attr);
        try {
            text = getText(0, getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
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
        int i;
        for (i = position; i < text.length() && text.charAt(i) != '\n'; i++);
        return i;
    }

    public int getLineStart(int position) {
        int i;
        for (i = position - 1; i >= 0 && text.charAt(i) != '\n'; i--);
        return i + 1;
    }

    private boolean isOnlyWhitespacesOnLine(int endPosition) {
        for (int start = getLineStart(endPosition), i = endPosition - 1; i >= start; i--) {
            if (!Character.isWhitespace(text.charAt(i))) return false;
        }
        return true;
    }

    public void updateSyntaxHighlighting() {
        updateSyntaxHighlighting2(0, getLength());
    }

    private boolean isInsideComment(int index) {
        return isInsideBlockComment(index) || isInsideDocComment(index);
    }

    private boolean isInsideLineComment(int index) {
        final int lineStart = getLineStart(index);
        final String linePart = text.substring(lineStart, index);
        return linePart.contains("//");
    }

    private boolean isInsideDocComment(int index) {
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

    private boolean isInsideBlockComment(int index) {
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

    private void updateSyntaxHighlighting2(int offset, int length) {
        // TODO If block comment is removed, update the ex-comment
        try {
            int lineStart = getLineStart(offset);
            int lineEnd = getLineEnd(offset + length);
            final boolean first = isInsideComment(lineStart);
            final boolean second = isInsideComment(lineEnd);
            if (first) {
                lineStart = text.substring(0, lineStart).lastIndexOf("/*");
            }
            if (second) {
                final int bce = text.indexOf("*/", lineEnd);
                lineEnd = bce == -1 ? text.length() : bce + 2;
            } else if (first) {
                final int bco = text.indexOf("*/", lineEnd);
                lineEnd = bco == -1 ? text.length() : bco + 1;
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

    /*public void updateSyntaxHighlightingOld() {
        try {
            idents.clear();
            List<Pair<Integer, Integer>> ideLocal = new ArrayList<>();
            Tokenizer tokenizer = new Tokenizer(new StringCharStream("", text));
            tokenizer.enableCommentTokens();
            Token token;
            int depth = 0;
            do {
                token = tokenizer.nextToken();
                if (token.is(TokenType.LCURLY)) {
                    depth++;
                    ideLocal.add(new Pair<>(token.getPosition().getIndex() - 1, depth));
                } else if (token.is(TokenType.RCURLY)) {
                    depth--;
                    ideLocal.add(new Pair<>(token.getPosition().getIndex(), depth));
                }
                Style style = theme.getStyle(token.getType()) == null ? def : theme.getStyle(token.getType()).asStyle(def);
                if (style == null) style = def;
                int pos = token.getPosition().getIndex();
                setCharacterAttributes(pos, token.getEndPosition().getIndex() - pos, style, true);
                // TODO Replace by the highlighter interface! - mhahnFr
                if (token.getType() == TokenType.IDENT) idents.add(token);
            } while (token.getType() != TokenType.EOF);
            ASTClass c = new Parser(new Tokenizer(new StringCharStream("", text))).parseFile();
            synchronized (this) {
                ide.clear();
                ide.addAll(ideLocal);
            }
        } catch (ParseException e) {
            Style s = theme.getErrorStyle() == null ? def : theme.getErrorStyle().asStyle(def);
            Token t = e.getToken();
            if (t != null)
               setCharacterAttributes(t.getPosition().getIndex(), t.getEndPosition().getIndex() - t.getPosition().getIndex(), s, false);
        }
    }*/
}
