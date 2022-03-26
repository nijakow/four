package nijakow.four.client.editor;

import nijakow.four.client.utils.StringHelper;
import nijakow.four.share.lang.c.ast.ASTClass;
import nijakow.four.share.lang.c.parser.*;
import nijakow.four.share.util.Pair;

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
    private final List<Token> idents;
    private final List<Pair<Integer, Integer>> ide;
    private final ExecutorService threads;

    public FDocument() {
        threads = Executors.newSingleThreadScheduledExecutor();
        def = getLogicalStyle(0);
        theme = FTheme.getDefaultTheme(def);
        idents = new ArrayList<>();
        ide = new ArrayList<>();
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
            this.theme = FTheme.getDefaultTheme(def);
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

    @Override
    protected void postRemoveUpdate(DefaultDocumentEvent chng) {
        super.postRemoveUpdate(chng);
        try {
            text = getText(0, getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        if (highlighting)
            threads.execute(this::updateSyntaxHighlighting);
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (autoIndenting) {
            if (str.equals("}") && isOnlyWhitespacesOnLine(offs)) {
                offs = fixIndentation(offs, true);
            } else if (str.equals("\n")) {
                str += StringHelper.generateFilledString(' ', getIndentationLevel(offs) * 4);
            }
        }
        super.insertString(offs, str, a);
    }

    @Override
    protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
        super.insertUpdate(chng, attr);
        try {
            text = getText(0, getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        if (highlighting)
            threads.execute(this::updateSyntaxHighlighting);
    }

    public void resetHighlight() {
        setCharacterAttributes(0, getLength(), def, true);
    }

    public List<FSuggestion> computeSuggestions(int cursorPosition) {
        List<FSuggestion> ret = new ArrayList<>();
        for (Token t : idents) {
            ret.add(new FSuggestion(t.getPayload().toString()));
        }
        return ret;
    }

    public boolean isOnlyWhitespacesOnLine(int endPosition) {
        char c;
        for (int i = endPosition - 1; i >= 0 && (c = text.charAt(i)) != '\n'; i--) {
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    public int fixIndentation(int position, boolean isClosingBrace) throws BadLocationException {
        int i;
        int j;
        for (i = position - 1; i >= 0 && text.charAt(i) != '\n'; i--);
        i++;
        for (j = i; j < text.length() && Character.isWhitespace(text.charAt(j)) && text.charAt(j) != '\n'; j++);
        getContent().remove(i, j - i);
        int newIndentation = getIndentationLevel(i);
        if (isClosingBrace) newIndentation--;
        if (newIndentation < 0) newIndentation = 0;
        insertString(i, StringHelper.generateFilledString(' ', newIndentation * 4), null);
        return i + newIndentation * 4;
    }

    public synchronized int getIndentationLevel(int indentationPosition) {
        int indent = 0;
        for (Pair<Integer, Integer> p : ide) {
            if (indentationPosition <= p.getFirst())
                return indent;
            indent = p.getSecond();
        }
        return indent;
    }

    public void updateSyntaxHighlighting() {
        try {
            idents.clear();
            List<Pair<Integer, Integer>> ideLocal = new ArrayList<>();
            Tokenizer tokenizer = new Tokenizer(new StringCharStream("", text));
            int lastEnd = 0;
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
                Style style = theme.getStyle(token.getType());
                if (style == null) style = def;
                int pos = token.getPosition().getIndex();
                setCharacterAttributes(pos, token.getEndPosition().getIndex() - pos, style, true);
                setCharacterAttributes(lastEnd, pos - lastEnd, theme.getStyle(null), true);
                // TODO Replace by the highlighter interface! - mhahnFr
                lastEnd = token.getEndPosition().getIndex();
                if (token.getType() == TokenType.IDENT) idents.add(token);
            } while (token.getType() != TokenType.EOF);
            ASTClass c = new Parser(new Tokenizer(new StringCharStream("", text))).parseFile();
            synchronized (this) {
                ide.clear();
                ide.addAll(ideLocal);
            }
        } catch (ParseException e) {
            Style s = theme.getErrorStyle();
            Token t = e.getToken();
            if (t != null)
               setCharacterAttributes(t.getPosition().getIndex(), t.getEndPosition().getIndex() - t.getPosition().getIndex(), s, false);
        }
    }
}
