package nijakow.four.client.editor;

//import nijakow.four.share.lang.c.parser.TokenType;
import nijakow.four.smalltalk.parser.TokenType;
import java.awt.Color;

public class DefaultTheme extends FTheme {
    public DefaultTheme() {
        /*FStyle ident = new FStyle(TokenType.IDENT);
        ident.setForeground(Color.orange);
        addStyle(TokenType.IDENT, ident);*/

        FStyle comment = new FStyle(TokenType.COMMENT);
        comment.setForeground(Color.gray);
        comment.setItalic(true);
        addStyle(TokenType.COMMENT, comment);

        FStyle doc = new FStyle(TokenType.DOCUMENTAR);
        doc.setForeground(new Color(0xB60117));
        doc.setItalic(true);
        addStyle(TokenType.DOCUMENTAR, doc);

        FStyle keyWord = new FStyle(TokenType.NIL);
        keyWord.setForeground(new Color(0x046382));
        //keyWord.setItalic(true);
        keyWord.setBold(true);
        addStyle(TokenType.NIL, keyWord);
        addStyle(TokenType.TRUE, new FStyle(TokenType.TRUE, keyWord));
        addStyle(TokenType.FALSE, new FStyle(TokenType.FALSE, keyWord));
        addStyle(TokenType.THISCONTEXT, new FStyle(TokenType.THISCONTEXT, keyWord));
        addStyle(TokenType.INTEGER, new FStyle(TokenType.INTEGER, keyWord));

        FStyle type = new FStyle(TokenType.STRING);
        type.setForeground(new Color(0x048282));
        addStyle(TokenType.STRING, type);
        addStyle(TokenType.SYMBOL, new FStyle(TokenType.SYMBOL, type));
        addStyle(TokenType.CHARACTER, new FStyle(TokenType.CHARACTER, type));
        /*addStyle(TokenType.ANY, new FStyle(TokenType.ANY, type));
        addStyle(TokenType.VOID, new FStyle(TokenType.VOID, type));
        addStyle(TokenType.BOOL, new FStyle(TokenType.BOOL, type));
        addStyle(TokenType.CHAR, new FStyle(TokenType.CHAR, type));
        addStyle(TokenType.FUNC, new FStyle(TokenType.FUNC, type));
        addStyle(TokenType.LIST, new FStyle(TokenType.LIST, type));
        addStyle(TokenType.STRING, new FStyle(TokenType.STRING, type));
        addStyle(TokenType.OBJECT, new FStyle(TokenType.OBJECT, type));
        addStyle(TokenType.MAPPING, new FStyle(TokenType.MAPPING, type));*/

        FStyle controlWord = new FStyle(TokenType.SELF);
        controlWord.setForeground(new Color(0xaba003));
        addStyle(TokenType.SELF, controlWord);
        addStyle(TokenType.SUPER, new FStyle(TokenType.SUPER, controlWord));

        FStyle id = new FStyle(TokenType.IDENTIFIER);
        id.setForeground(new Color(0xb30bbf));
        addStyle(TokenType.IDENTIFIER, id);

        FStyle flowControl = new FStyle(TokenType.CARET);
        flowControl.setForeground(new Color(0x167505));
        addStyle(TokenType.CARET, flowControl);
        addStyle(TokenType.ASSIGN, flowControl);

        /*FStyle controlWord = new FStyle(TokenType.IF);
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
        addStyle(TokenType.INHERITS, new FStyle(TokenType.INHERITS, controlWord));*/

        /*FStyle constant = new FStyle(TokenType.CONSTANT);
        constant.setForeground(Color.blue);
        constant.setItalic(true);
        addStyle(TokenType.CONSTANT, constant);*/

        /*FStyle c_doc = new FStyle(TokenType.C_DOC);
        c_doc.setForeground(Color.blue);
        c_doc.setItalic(true);
        addStyle(TokenType.C_DOC, c_doc);*/

        /*FStyle comment = new FStyle(TokenType.COMMENT);
        comment.setItalic(true);
        comment.setForeground(Color.gray);
        addStyle(TokenType.COMMENT, comment);*/
    }
}
