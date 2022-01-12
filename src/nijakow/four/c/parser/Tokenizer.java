package nijakow.four.c.parser;

import java.util.Stack;

import nijakow.four.c.ast.OperatorType;
import nijakow.four.runtime.FInteger;
import nijakow.four.runtime.FString;

public class Tokenizer {	
	private CharStream stream;
	private Stack<Token> pushbacks;

	public StreamPosition getPosition() {
		return stream.getPosition();
	}
	
	private boolean isSpecial(int c) {
		return !(Character.isAlphabetic(c) || Character.isDigit(c) || c == '_' || c == '$');
	}
	
	private boolean peeks(String s) {
		return stream.peeks(s);
	}
	
	private void skipWhitespace() {
		while (Character.isWhitespace(stream.peek()))
			stream.advance();
	}
	
	private void skipLineComment() {
		while (stream.peek() != '\n')
			stream.advance();
	}
	
	private void skipBlockComment() {
		while (!stream.peeks("*/"))
			stream.advance();
	}
	
	private int parseChar(char terminator) {
		char c = (char) stream.next();
		if (c == terminator)
			return -1;
		else if (c == '\\') {
			c = (char) stream.next();
			if (c == '\\') return '\\';
			else if (c == 't') return '\t';
			else if (c == 'n') return '\n';
			else if (c == 'r') return '\r';
			else if (c == 'b') return '\b';
			else if (c == '0') return '\0';
			else if (c == 'a') return '\07';
			else if (c == 'e') return '\033';
			else if (c == '{') return '\02';
			else if (c == '}') return '\03';
			else if (c == terminator) return terminator;
			else return c;
		} else {
			return c;
		}
	}
	
	private String parseString(char terminator) {
		StringBuilder builder = new StringBuilder();
		
		while (stream.peek() >= 0) {
			int c = parseChar(terminator);
			if (c != -1) builder.append((char) c);
			else break;
		}
		
		return builder.toString();
	}
	
	public Token nextToken() throws ParseException {
		if (!pushbacks.isEmpty())
			return pushbacks.pop();
		
		skipWhitespace();
		
		StreamPosition pos = stream.getPosition();
		
		if (peeks("//")) {
			skipLineComment();
			return nextToken();
		} else if (peeks("/*")) {
			skipBlockComment();
			return nextToken();
		} else if (peeks("(")) return new Token(this, pos, TokenType.LPAREN);
		else if (peeks(")")) return new Token(this, pos, TokenType.RPAREN);
		else if (peeks("[")) return new Token(this, pos, TokenType.LBRACK);
		else if (peeks("]")) return new Token(this, pos, TokenType.RBRACK);
		else if (peeks("{")) return new Token(this, pos, TokenType.LCURLY);
		else if (peeks("}")) return new Token(this, pos, TokenType.RCURLY);
		else if (peeks("...")) return new Token(this, pos, TokenType.ELLIPSIS);
		else if (peeks("::")) return new Token(this, pos, TokenType.SCOPE);
		else if (peeks(".")) return new Token(this, pos, TokenType.DOT);
		else if (peeks(",")) return new Token(this, pos, TokenType.COMMA);
		else if (peeks(":")) return new Token(this, pos, TokenType.COLON);
		else if (peeks(";")) return new Token(this, pos, TokenType.SEMICOLON);
		else if (peeks("->")) return new Token(this, pos, TokenType.RARROW);
		else if (peeks("++")) return new Token(this, pos, TokenType.INC1);
		else if (peeks("--")) return new Token(this, pos, TokenType.DEC1);
		else if (peeks("*")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.MULT, -1, 3, true));
		else if (peeks("/")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.DIV, -1, 3, true));
		else if (peeks("%")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.MOD, -1, 3, true));
		else if (peeks("+")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.PLUS, -1, 4, true));
		else if (peeks("-")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.MINUS, -1, 4, true));
		else if (peeks("<<")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.SHL, -1, 5, true));
		else if (peeks(">>")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.SHR, -1, 5, true));
		else if (peeks("<=")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.LEQ, -1, 6, true));
		else if (peeks(">=")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.GEQ, -1, 6, true));
		else if (peeks("<")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.LESS, -1, 6, true));
		else if (peeks(">")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.GREATER, -1, 6, true));
		else if (peeks("==")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.EQ, -1, 7, true));
		else if (peeks("!=")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.INEQ, -1, 7, true));
		else if (peeks("==")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.EQ, -1, 7, true));
		else if (peeks("&&")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.LOGAND, -1, 11, true));
		else if (peeks("||")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.LOGOR, -1, 12, true));
		else if (peeks("&")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.BITAND, -1, 8, true));
		else if (peeks("^")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.BITXOR, -1, 9, true));
		else if (peeks("|")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.BITOR, -1, 10, true));
		else if (peeks("!")) return new Token(this, pos, TokenType.OPERATOR, new OperatorInfo(OperatorType.LOGNOT, 2, -1, true));
		else if (peeks("=")) return new Token(this, pos, TokenType.ASSIGNMENT);
		else if (peeks("\"")) return new Token(this, pos, TokenType.CONSTANT, new FString(parseString('\"')));
		else if (peeks("\'")) {
			char c = (char) parseChar('\'');
			if (stream.next() != '\'')
				throw new ParseException(pos, "Expected \"\'\"!");
			return new Token(this, pos, TokenType.CONSTANT, new FInteger(c));
		}
		
		
		StringBuilder builder = new StringBuilder();
		while (!isSpecial(stream.peek())) {
			builder.append((char) stream.next());
		}
		
		final String text = builder.toString();
		
		try {
			return new Token(this, pos, TokenType.CONSTANT, new FInteger(Integer.parseInt(text)));
		} catch (NumberFormatException e) {
		}
		
		switch (text) {
		case "": return new Token(this, pos, TokenType.EOF);
		case "this": return new Token(this, pos, TokenType.THIS);
		case "nil": return new Token(this, pos, TokenType.NIL);
		case "true": return new Token(this, pos, TokenType.TRUE);
		case "false": return new Token(this, pos, TokenType.FALSE);
		case "use": return new Token(this, pos, TokenType.USE);
		case "inherit": return new Token(this, pos, TokenType.INHERIT);
		case "any": return new Token(this, pos, TokenType.ANY);
		case "void": return new Token(this, pos, TokenType.VOID);
		case "int": return new Token(this, pos, TokenType.INT);
		case "char": return new Token(this, pos, TokenType.CHAR);
		case "bool": return new Token(this, pos, TokenType.BOOL);
		case "string": return new Token(this, pos, TokenType.STRING);
		case "object": return new Token(this, pos, TokenType.OBJECT);
		case "func": return new Token(this, pos, TokenType.FUNC);
		case "list": return new Token(this, pos, TokenType.LIST);
		case "mapping": return new Token(this, pos, TokenType.MAPPING);
		case "if": return new Token(this, pos, TokenType.IF);
		case "else": return new Token(this, pos, TokenType.ELSE);
		case "while": return new Token(this, pos, TokenType.WHILE);
		case "for": return new Token(this, pos, TokenType.FOR);
		case "foreach": return new Token(this, pos, TokenType.FOREACH);
		case "break": return new Token(this, pos, TokenType.BREAK);
		case "continue": return new Token(this, pos, TokenType.CONTINUE);
		case "return": return new Token(this, pos, TokenType.RETURN);
		case "va_next": return new Token(this, pos, TokenType.VA_NEXT);
		case "va_count": return new Token(this, pos, TokenType.VA_COUNT);
		default: return new Token(this, pos, TokenType.IDENT, text);
		}
	}
	
	void unread(Token t) {
		pushbacks.push(t);
	}

	public Tokenizer(CharStream stream) {
		this.stream = stream;
		this.pushbacks = new Stack<>();
	}
}
