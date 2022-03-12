package nijakow.four.client.editor;

import nijakow.four.client.Commands;
import nijakow.four.share.lang.c.parser.*;

import javax.swing.text.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FDocument extends DefaultStyledDocument {
    private final Style def;
    private String text;
    private boolean highlighting;
    private List<Token> tokens;
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
            int lastEnd = 0;
            for (Token token : tokens) {
                 setCharacterAttributes(lastEnd, token.getPosition().getIndex() - lastEnd, def, true);
                 switch (token.getType()) {
                     case NIL:
                     case TRUE:
                     case FALSE: setCharacterAttributes(token.getPosition().getIndex(), token.getEndPosition().getIndex() - token.getPosition().getIndex(), getStyle(Commands.Styles.STYLE_SPECIAL_WORD), true); break;

                     case IF:
                     case USE:
                     case FOR:
                     case NEW:
                     case THIS:
                     case ELSE:
                     case BREAK:
                     case WHILE:
                     case CLASS:
                     case RETURN:
                     case PUBLIC:
                     case STRUCT:
                     case PRIVATE:
                     case CONTINUE:
                     case INHERITS: setCharacterAttributes(token.getPosition().getIndex(), token.getEndPosition().getIndex() - token.getPosition().getIndex(), getStyle(Commands.Styles.STYLE_KEYWORD), true); break;

                     case ANY:
                     case INT:
                     case VOID:
                     case CHAR:
                     case BOOL:
                     case LIST:
                     case STRING:
                     case OBJECT:
                     case MAPPING: setCharacterAttributes(token.getPosition().getIndex(), token.getEndPosition().getIndex() - token.getPosition().getIndex(), getStyle(Commands.Styles.STYLE_TYPE), true); break;

                     case IDENT: setCharacterAttributes(token.getPosition().getIndex(), token.getPosition().getIndex() + token.getPayload().toString().length(), getStyle(Commands.Styles.STYLE_STDLIB), true); break;

                     default: setCharacterAttributes(token.getPosition().getIndex(), token.getEndPosition().getIndex() - token.getPosition().getIndex(), def, true); break;
                }
                lastEnd = token.getEndPosition().getIndex();
            }
        } catch (ParseException e) {
            // TODO
        }
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
