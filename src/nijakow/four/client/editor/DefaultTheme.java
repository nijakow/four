package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;

public class DefaultTheme extends FTheme {
    public DefaultTheme(Style defaultStyle) {
        StyleContext context = new StyleContext();
        Style s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.orange);
        addStyle(TokenType.IDENT, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setItalic(s, true);
        StyleConstants.setForeground(s, Color.blue);
        addStyle(TokenType.NIL, s);
        addStyle(TokenType.TRUE, s);
        addStyle(TokenType.FALSE, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.green);
        addStyle(TokenType.INT, s);
        addStyle(TokenType.ANY, s);
        addStyle(TokenType.VOID, s);
        addStyle(TokenType.BOOL, s);
        addStyle(TokenType.CHAR, s);
        addStyle(TokenType.FUNC, s);
        addStyle(TokenType.LIST, s);
        addStyle(TokenType.STRING, s);
        addStyle(TokenType.OBJECT, s);
        addStyle(TokenType.MAPPING, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.red);
        StyleConstants.setBold(s, true);
        addStyle(TokenType.IF, s);
        addStyle(TokenType.USE, s);
        addStyle(TokenType.FOR, s);
        addStyle(TokenType.NEW, s);
        addStyle(TokenType.THIS, s);
        addStyle(TokenType.ELSE, s);
        addStyle(TokenType.BREAK, s);
        addStyle(TokenType.WHILE, s);
        addStyle(TokenType.CLASS, s);
        addStyle(TokenType.RETURN, s);
        addStyle(TokenType.PUBLIC, s);
        addStyle(TokenType.STRUCT, s);
        addStyle(TokenType.FOREACH, s);
        addStyle(TokenType.PRIVATE, s);
        addStyle(TokenType.CONTINUE, s);
        addStyle(TokenType.INHERITS, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.blue);
        StyleConstants.setItalic(s, true);
        addStyle(TokenType.CONSTANT, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.blue);
        StyleConstants.setItalic(s, true);
        StyleConstants.setBold(s, true);
        addStyle(TokenType.C_DOC, s);

        s = context.addStyle(null, defaultStyle);
        StyleConstants.setForeground(s, Color.gray);
        StyleConstants.setItalic(s, true);
        addStyle(TokenType.COMMENT, s);
    }
}
