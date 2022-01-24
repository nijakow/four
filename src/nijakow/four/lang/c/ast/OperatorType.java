package nijakow.four.lang.c.ast;

public enum OperatorType {
	PLUS, MINUS, MULT, DIV, MOD,
	UPLUS, UMINUS,
	INCREMENT1, DECREMENT1,
	SHL, SHR, BITAND, BITXOR, BITOR, BITNOT,
	LOGAND, LOGOR, LOGNOT,
	EQ, INEQ, LEQ, LESS, GEQ, GREATER,
	LENGTH /* Special operator for array bounds */
}
