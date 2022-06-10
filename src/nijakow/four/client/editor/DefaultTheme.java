package nijakow.four.client.editor;

import nijakow.four.smalltalk.parser.TokenType;
import java.awt.Color;

public class DefaultTheme extends FTheme {
    public DefaultTheme() {
        TokenStyle comment = new TokenStyle(TokenType.COMMENT);
        comment.setForeground(Color.gray);
        comment.setItalic(true);
        addStyle(TokenType.COMMENT, comment);

        TokenStyle doc = new TokenStyle(TokenType.DOCUMENTAR);
        doc.setForeground(new Color(0xB60117));
        doc.setItalic(true);
        addStyle(TokenType.DOCUMENTAR, doc);

        TokenStyle keyWord = new TokenStyle(TokenType.NIL);
        keyWord.setForeground(new Color(0x046382));
        keyWord.setBold(true);
        addStyle(TokenType.NIL, keyWord);
        addStyle(TokenType.TRUE, new TokenStyle(TokenType.TRUE, keyWord));
        addStyle(TokenType.FALSE, new TokenStyle(TokenType.FALSE, keyWord));
        addStyle(TokenType.THISCONTEXT, new TokenStyle(TokenType.THISCONTEXT, keyWord));
        addStyle(TokenType.INTEGER, new TokenStyle(TokenType.INTEGER, keyWord));

        TokenStyle type = new TokenStyle(TokenType.STRING);
        type.setForeground(new Color(0x048282));
        addStyle(TokenType.STRING, type);
        addStyle(TokenType.SYMBOL, new TokenStyle(TokenType.SYMBOL, type));
        addStyle(TokenType.CHARACTER, new TokenStyle(TokenType.CHARACTER, type));

        TokenStyle controlWord = new TokenStyle(TokenType.SELF);
        controlWord.setForeground(new Color(0xaba003));
        addStyle(TokenType.SELF, controlWord);
        addStyle(TokenType.SUPER, new TokenStyle(TokenType.SUPER, controlWord));
        addStyle(TokenType.PRIMITIVE, new TokenStyle(TokenType.PRIMITIVE, controlWord));

        TokenStyle id = new TokenStyle(TokenType.IDENTIFIER);
        id.setForeground(new Color(0xb30bbf));
        addStyle(TokenType.IDENTIFIER, id);

        TokenStyle flowControl = new TokenStyle(TokenType.CARET);
        flowControl.setForeground(new Color(0x167505));
        addStyle(TokenType.CARET, flowControl);
        addStyle(TokenType.ASSIGN, new TokenStyle(TokenType.ASSIGN, flowControl));
    }
}
