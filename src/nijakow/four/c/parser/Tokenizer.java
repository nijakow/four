package nijakow.four.c.parser;

import java.util.Stack;

import nijakow.four.c.ast.OperatorType;
import nijakow.four.c.runtime.FInteger;
import nijakow.four.c.runtime.FString;

public class Tokenizer {	
	private CharStream stream;
	private Stack<Token> pushbacks;
	
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
	
	public Token nextToken() {
		if (!pushbacks.isEmpty())
			return pushbacks.pop();
		
		skipWhitespace();
		
		
		if (peeks("//")) {
			skipLineComment();
			return nextToken();
		} else if (peeks("/*")) {
			skipBlockComment();
			return nextToken();
		} else if (peeks("(")) return new Token(this, TokenType.LPAREN);
		else if (peeks(")")) return new Token(this, TokenType.RPAREN);
		else if (peeks("[")) return new Token(this, TokenType.LBRACK);
		else if (peeks("]")) return new Token(this, TokenType.RBRACK);
		else if (peeks("{")) return new Token(this, TokenType.LCURLY);
		else if (peeks("}")) return new Token(this, TokenType.RCURLY);
		else if (peeks("...")) return new Token(this, TokenType.ELLIPSIS);
		else if (peeks("::")) return new Token(this, TokenType.SCOPE);
		else if (peeks(".")) return new Token(this, TokenType.DOT);
		else if (peeks(",")) return new Token(this, TokenType.COMMA);
		else if (peeks(":")) return new Token(this, TokenType.COLON);
		else if (peeks(";")) return new Token(this, TokenType.SEMICOLON);
		else if (peeks("->")) return new Token(this, TokenType.RARROW);
		else if (peeks("++")) return new Token(this, TokenType.INC1);
		else if (peeks("--")) return new Token(this, TokenType.DEC1);
		else if (peeks("*")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.MULT, -1, 3, true));
		else if (peeks("/")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.DIV, -1, 3, true));
		else if (peeks("%")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.MOD, -1, 3, true));
		else if (peeks("+")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.PLUS, -1, 4, true));
		else if (peeks("-")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.MINUS, -1, 4, true));
		else if (peeks("<<")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.SHL, -1, 5, true));
		else if (peeks(">>")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.SHR, -1, 5, true));
		else if (peeks("<=")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.LEQ, -1, 6, true));
		else if (peeks(">=")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.GEQ, -1, 6, true));
		else if (peeks("<")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.LESS, -1, 6, true));
		else if (peeks(">")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.GREATER, -1, 6, true));
		else if (peeks("==")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.EQ, -1, 7, true));
		else if (peeks("!=")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.INEQ, -1, 7, true));
		else if (peeks("==")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.EQ, -1, 7, true));
		else if (peeks("&&")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.LOGAND, -1, 11, true));
		else if (peeks("||")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.LOGOR, -1, 12, true));
		else if (peeks("&")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.BITAND, -1, 8, true));
		else if (peeks("^")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.BITXOR, -1, 9, true));
		else if (peeks("|")) return new Token(this, TokenType.OPERATOR, new OperatorInfo(OperatorType.BITOR, -1, 10, true));
		else if (peeks("=")) return new Token(this, TokenType.ASSIGNMENT);
		else if (peeks("\"")) return new Token(this, TokenType.CONSTANT, new FString(parseString('\"')));
		else if (peeks("\'")) {
			char c = (char) parseChar('\'');
			if (stream.next() != '\'')
				throw new RuntimeException("Expected \"\'\"!");
			return new Token(this, TokenType.CONSTANT, new FInteger(c));
		}
		
		
		StringBuilder builder = new StringBuilder();
		while (!isSpecial(stream.peek())) {
			builder.append((char) stream.next());
		}
		
		final String text = builder.toString();
		
		try {
			return new Token(this, TokenType.CONSTANT, new FInteger(Integer.parseInt(text)));
		} catch (NumberFormatException e) {
		}
		
		switch (text) {
		case "": return new Token(this, TokenType.EOF);
		case "this": return new Token(this, TokenType.THIS);
		case "nil": return new Token(this, TokenType.NIL);
		case "use": return new Token(this, TokenType.USE);
		case "inherit": return new Token(this, TokenType.INHERIT);
		case "any": return new Token(this, TokenType.ANY);
		case "void": return new Token(this, TokenType.VOID);
		case "int": return new Token(this, TokenType.INT);
		case "char": return new Token(this, TokenType.CHAR);
		case "bool": return new Token(this, TokenType.BOOL);
		case "string": return new Token(this, TokenType.STRING);
		case "object": return new Token(this, TokenType.OBJECT);
		case "if": return new Token(this, TokenType.IF);
		case "else": return new Token(this, TokenType.ELSE);
		case "while": return new Token(this, TokenType.WHILE);
		case "for": return new Token(this, TokenType.FOR);
		case "return": return new Token(this, TokenType.RETURN);
		case "va_next": return new Token(this, TokenType.VA_NEXT);
		case "va_count": return new Token(this, TokenType.VA_COUNT);
		default: return new Token(this, TokenType.IDENT, text);
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
