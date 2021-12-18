package nijakow.four.c.parser;

public enum TokenType {
	EOF,
	IDENT, CONSTANT, OPERATOR,
	LPAREN, RPAREN,
	LBRACK, RBRACK,
	LCURLY, RCURLY,
	DOT, COMMA, COLON, SEMICOLON,
	RARROW,
	ASSIGNMENT, INC1, DEC1,
	SCOPE, ELLIPSIS, VA_NEXT, VA_COUNT,
	THIS, USE, INHERIT,
	ANY, VOID,
	IF, ELSE, WHILE, FOR,
	RETURN
}
