package nijakow.four.share.lang.c.parser;

public enum TokenType {
	EOF,
	IDENT, CONSTANT, OPERATOR,
	LPAREN, RPAREN,
	LBRACK, RBRACK,
	LCURLY, RCURLY,
	DOT, COMMA, COLON, SEMICOLON,
	RARROW,
	ASSIGNMENT, INC1, DEC1,
	SCOPE, RDOUBLEARROW, ELLIPSIS, VA_NEXT, VA_COUNT,
	THIS, NIL, TRUE, FALSE, USE, INHERITS,
	ANY, VOID, INT, CHAR, BOOL, STRING, OBJECT, FUNC, LIST, MAPPING,
	STRUCT, CLASS, NEW,
	IF, ELSE, WHILE, FOR, FOREACH, BREAK, CONTINUE, RETURN
}
