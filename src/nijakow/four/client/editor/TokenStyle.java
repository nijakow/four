package nijakow.four.client.editor;

import nijakow.four.smalltalk.parser.TokenType;

import javax.swing.text.Style;

public class TokenStyle extends FStyle {
    private TokenType tokenType;

    public TokenStyle() {
        this((TokenType) null);
    }

    public TokenStyle(Style style) {
        super(style);
    }

    public TokenStyle(FStyle original, boolean resolveInheritance) {
        super(original, resolveInheritance);
        if (original instanceof TokenStyle) {
            tokenType = ((TokenStyle) original).getTokenType();
        }
    }

    public TokenStyle(TokenType tokenType) {
        this(tokenType, null);
    }

    public TokenStyle(TokenType tokenType, FStyle parent) {
        super(parent);
        this.tokenType = tokenType;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(TokenType tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public TokenStyle getParent() {
        final FStyle parent = super.getParent();
        return parent instanceof TokenStyle ? (TokenStyle) parent : null;
    }
}
