package nijakow.four.client.editor;

import nijakow.four.client.Commands;

import javax.swing.text.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FDocument extends DefaultStyledDocument {
    private final Style def;
    private String text;
    private boolean highlighting;
    private final ExecutorService threads;

    public FDocument() {
        threads = Executors.newSingleThreadScheduledExecutor();
        def = getLogicalStyle(0);
        try {
            text = getText(0, getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        addStyles();
    }

    public FDocument(boolean highlighting) {
        this();
        this.highlighting = highlighting;
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
        resetHighlight();
        String keywords = "\\b(new|struct|class|inherits|use|this|if|else|while|for|break|continue|switch|case|return|private|protected|public)\\b";
        Matcher matcher = Pattern.compile(keywords).matcher(text);
        while (matcher.find())
            setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
                    getStyle(Commands.Styles.STYLE_KEYWORD), true);
        keywords = "\\b(any|void|int|char|bool|string|object|list|mapping)\\b";
        matcher = Pattern.compile(keywords).matcher(text);
        while (matcher.find())
            setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
                    getStyle(Commands.Styles.STYLE_TYPE), true);
        keywords = "\\b(true|false|nil|va_next|va_count)\\b";
        matcher = Pattern.compile(keywords).matcher(text);
        while (matcher.find())
            setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
                    getStyle(Commands.Styles.STYLE_SPECIAL_WORD), true);
        keywords = "\\b(create|the|call|log|length|insert|append|remove|strlen|chr|write|prompt|password|edit)\\b";
        matcher = Pattern.compile(keywords).matcher(text);
        while (matcher.find())
            setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
                    getStyle(Commands.Styles.STYLE_STDLIB), true);
    }

    private void addStyles() {
        Style s = addStyle(Commands.Styles.STYLE_KEYWORD, def);
        StyleConstants.setForeground(s, Color.red);
        StyleConstants.setBold(s, true);
        s = addStyle(Commands.Styles.STYLE_TYPE, def);
        StyleConstants.setForeground(s, Color.green);
        s = addStyle(Commands.Styles.STYLE_SPECIAL_WORD, def);
        StyleConstants.setItalic(s, true);
        StyleConstants.setForeground(s, Color.blue);
        s = addStyle(Commands.Styles.STYLE_STDLIB, def);
        StyleConstants.setForeground(s, Color.orange);
    }
}
