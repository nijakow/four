package nijakow.four.client.editor;

import nijakow.four.server.runtime.objects.standard.FString;
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
    private FTheme theme;
    private final List<Token> idents;
    private final ExecutorService threads;

    public FDocument() {
        threads = Executors.newSingleThreadScheduledExecutor();
        def = getLogicalStyle(0);
        theme = FTheme.getDefaultTheme(def);
        idents = new ArrayList<>();
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
            //if (!ret.contains(t.getPayload().toString()))
            ret.add(new FSuggestion(t.getPayload().toString()));
        }
        return ret;
    }

    public void updateSyntaxHighlighting() {
        try {
            idents.clear();
            Tokenizer tokenizer = new Tokenizer(new StringCharStream("", text));
            int lastEnd = 0;
            Token token;
            do {
                token = tokenizer.nextToken();
                Style style = theme.getStyle(token.getType());
                if (style == null) style = def;
                int pos = token.getPosition().getIndex();
                setCharacterAttributes(pos, token.getEndPosition().getIndex() - pos, style, true);
                setCharacterAttributes(lastEnd, pos - lastEnd, theme.getStyle(null), true);
                // TODO Replace by the highlighter interface! - mhahnFr
                lastEnd = token.getEndPosition().getIndex();
                if (token.getType() == TokenType.IDENT) idents.add(token);
            } while (token.getType() != TokenType.EOF);
        } catch (ParseException e) {
            // TODO
        }
    }
}
