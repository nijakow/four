package nijakow.four.client.editor;

import nijakow.four.share.lang.c.parser.TokenType;

import java.awt.Color;

public class DefaultTheme extends FTheme {
    public DefaultTheme() {
        FStyle ident = new FStyle(TokenType.IDENT);
        ident.setForeground(Color.orange);
        addStyle(TokenType.IDENT, ident);

        FStyle keyWord = new FStyle(TokenType.NIL);
        keyWord.setForeground(Color.blue);
        keyWord.setItalic(true);
        addStyle(TokenType.NIL, keyWord);
        addStyle(TokenType.TRUE, new FStyle(TokenType.TRUE, keyWord));
        addStyle(TokenType.FALSE, new FStyle(TokenType.FALSE, keyWord));

        FStyle type = new FStyle(TokenType.INT);
        type.setForeground(Color.green);
        addStyle(TokenType.INT, type);
        addStyle(TokenType.ANY, new FStyle(TokenType.ANY, type));
        addStyle(TokenType.VOID, new FStyle(TokenType.VOID, type));
        addStyle(TokenType.BOOL, new FStyle(TokenType.BOOL, type));
        addStyle(TokenType.CHAR, new FStyle(TokenType.CHAR, type));
        addStyle(TokenType.FUNC, new FStyle(TokenType.FUNC, type));
        addStyle(TokenType.LIST, new FStyle(TokenType.LIST, type));
        addStyle(TokenType.STRING, new FStyle(TokenType.STRING, type));
        addStyle(TokenType.OBJECT, new FStyle(TokenType.OBJECT, type));
        addStyle(TokenType.MAPPING, new FStyle(TokenType.MAPPING, type));

        FStyle controlWord = new FStyle(TokenType.IF);
        controlWord.setForeground(Color.red);
        controlWord.setBold(true);
        addStyle(TokenType.IF, controlWord);
        addStyle(TokenType.USE, new FStyle(TokenType.USE, controlWord));
        addStyle(TokenType.FOR, new FStyle(TokenType.FOR, controlWord));
        addStyle(TokenType.NEW, new FStyle(TokenType.NEW, controlWord));
        addStyle(TokenType.THIS, new FStyle(TokenType.THIS, controlWord));
        addStyle(TokenType.THISDOT, new FStyle(TokenType.THISDOT, controlWord));
        addStyle(TokenType.ELSE, new FStyle(TokenType.ELSE, controlWord));
        addStyle(TokenType.BREAK, new FStyle(TokenType.BREAK, controlWord));
        addStyle(TokenType.WHILE, new FStyle(TokenType.WHILE, controlWord));
        addStyle(TokenType.CLASS, new FStyle(TokenType.CLASS, controlWord));
        addStyle(TokenType.RETURN, new FStyle(TokenType.RETURN, controlWord));
        addStyle(TokenType.PUBLIC, new FStyle(TokenType.PUBLIC, controlWord));
        addStyle(TokenType.STRUCT, new FStyle(TokenType.STRUCT, controlWord));
        addStyle(TokenType.FOREACH, new FStyle(TokenType.FOREACH, controlWord));
        addStyle(TokenType.PRIVATE, new FStyle(TokenType.PRIVATE, controlWord));
        addStyle(TokenType.CONTINUE, new FStyle(TokenType.CONTINUE, controlWord));
        addStyle(TokenType.INHERITS, new FStyle(TokenType.INHERITS, controlWord));

        FStyle constant = new FStyle(TokenType.CONSTANT);
        constant.setForeground(Color.blue);
        constant.setItalic(true);
        addStyle(TokenType.CONSTANT, constant);

        FStyle c_doc = new FStyle(TokenType.C_DOC);
        c_doc.setForeground(Color.blue);
        c_doc.setItalic(true);
        addStyle(TokenType.C_DOC, c_doc);

        FStyle comment = new FStyle(TokenType.COMMENT);
        comment.setItalic(true);
        comment.setForeground(Color.gray);
        addStyle(TokenType.COMMENT, comment);
    }
}
