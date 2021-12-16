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
	THIS, USE,
	ANY,
	IF, ELSE, WHILE, FOR,
	RETURN
}
