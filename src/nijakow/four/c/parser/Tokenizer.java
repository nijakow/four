package nijakow.four.c.parser;

import java.util.Stack;

import nijakow.four.c.ast.OperatorType;
import nijakow.four.c.runtime.FInteger;
import nijakow.four.c.runtime.FString;

public class Tokenizer {	
	private CharStream stream;
	private Stack<Token> pushbacks;
	
	private boolean isSpecial(int c) {
		return !Character.isAlphabetic(c) && !Character.isDigit(c);
	}
	
	private boolean peeks(String s) {
		return stream.peeks(s);
	}
	
	private void skipWhitespace() {
		while (Character.isWhitespace(stream.peek()))
			stream.advance();
	}
	
	private String parseString(char terminator) {
		StringBuilder builder = new StringBuilder();
		
		while (stream.peek() >= 0) {
			char c = (char) stream.next();
			if (c == terminator)
				break;
			else if (c == '\\') {
				c = (char) stream.next();
				if (c == '\\') builder.append('\\');
				else if (c == 'n') builder.append('\n');
				else if (c == 't') builder.append('\t');
				else if (c == terminator) builder.append(terminator);
				else { builder.append('\\'); builder.append(c); }
			} else {
				builder.append(c);
			}
		}
		
		return builder.toString();
	}
	
	public Token nextToken() {
		if (!pushbacks.isEmpty())
			return pushbacks.pop();
		
		skipWhitespace();
		
		if (peeks("(")) return new Token(this, TokenType.LPAREN);
		else if (peeks(")")) return new Token(this, TokenType.RPAREN);
		else if (peeks("[")) return new Token(this, TokenType.LBRACK);
		else if (peeks("]")) return new Token(this, TokenType.RBRACK);
		else if (peeks("{")) return new Token(this, TokenType.LCURLY);
		else if (peeks("}")) return new Token(this, TokenType.RCURLY);
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
		case "any": return new Token(this, TokenType.ANY);
		case "if": return new Token(this, TokenType.IF);
		case "else": return new Token(this, TokenType.ELSE);
		case "while": return new Token(this, TokenType.WHILE);
		case "return": return new Token(this, TokenType.RETURN);
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
