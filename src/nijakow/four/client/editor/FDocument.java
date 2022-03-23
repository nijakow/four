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
    private final ExecutorService threads;

    public FDocument() {
        threads = Executors.newSingleThreadScheduledExecutor();
        def = getLogicalStyle(0);
        theme = FTheme.getDefaultTheme(def);
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

    public List<?> computeSuggestions(int cursorPosition) {
        List<Object> ret = new ArrayList<>();
        ret.add("TODO:");
        ret.add("Implement");
        ret.add("suggestion");
        ret.add("computation!");
        return ret;
    }

    public void updateSyntaxHighlighting() {
        try {
            Tokenizer tokenizer = new Tokenizer(new StringCharStream("", text));
            int length, lastEnd = 0;
            boolean wasCDOC = false;
            Token token;
            do {
                token = tokenizer.nextToken();
                boolean fStr = false;
                Style style = theme.getStyle(token.getType());
                int pos = token.getPosition().getIndex();
                if (style == null) style = def;
                if (token.getType() != TokenType.C_DOC && token.getPayload() != null && token.getPayload() instanceof String) {
                    length = token.getPosition().getIndex() + token.getPayload().toString().length();
                } else if (token.getPayload() != null && token.getPayload() instanceof FString) {
                    fStr = true;
                    length = ((FString) token.getPayload()).asString().length() + 2;
                } else {
                    length = token.getEndPosition().getIndex() - pos;
                }
                setCharacterAttributes(pos, length, style, true);
                setCharacterAttributes(lastEnd, pos - lastEnd, wasCDOC ? theme.getStyle(TokenType.C_DOC) : theme.getStyle(null), true);
                wasCDOC = token.getType() == TokenType.C_DOC;
                lastEnd = token.getEndPosition().getIndex();
                if (fStr) lastEnd += length - 1;
            } while (token.getType() != TokenType.EOF);
        } catch (ParseException e) {
            // TODO
        }
    }
}
