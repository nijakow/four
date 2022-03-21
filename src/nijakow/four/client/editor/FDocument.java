package nijakow.four.client.editor;

import nijakow.four.server.runtime.objects.standard.FString;
import nijakow.four.share.lang.c.parser.*;

import javax.swing.text.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FDocument extends DefaultStyledDocument {
    private final Style def;
    private String text;
    private boolean highlighting;
    private List<Token> tokens;
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

    public List<Token> parse() throws ParseException {
        ArrayList<Token> tokens = new ArrayList<>();
        Tokenizer tokenizer = new Tokenizer(new StringCharStream("", text));
        for (Token token = tokenizer.nextToken(); token.getType() != TokenType.EOF; token = tokenizer.nextToken()) {
            tokens.add(token);
        }
        return tokens;
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

    public void updateSyntaxHighlighting() {
        try {
            tokens = parse();
            int length, lastEnd = 0;
            boolean wasCDOC = false;
            for (Token token : tokens) {
                boolean fStr = false;
                Style style = theme.getStyle(token.getType());
                int pos = token.getPosition().getIndex();
                if (style == null) style = def;
                if (token.getType() == TokenType.C_DOC && token.getPayload() != null && token.getPayload() instanceof String) {
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
            }
        } catch (ParseException e) {
            // TODO
        }
    }
}
