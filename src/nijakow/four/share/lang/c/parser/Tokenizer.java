package nijakow.four.share.lang.c.parser;

import java.util.Stack;

import nijakow.four.share.lang.c.ast.OperatorType;
import nijakow.four.server.runtime.objects.standard.FInteger;
import nijakow.four.server.runtime.objects.standard.FString;

public class Tokenizer {	
	private final CharStream stream;
	private final Stack<Token> pushbacks;

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
		while (stream.peek() != '\n' && stream.peek() >= 0)
			stream.advance();
	}
	
	private String parseBlockComment() {
		StringBuilder builder = new StringBuilder();
		while (!stream.peeks("*/")) {
			int c = stream.next();
			if (c < 0) break;
			builder.append((char) c);
		}
		return builder.toString();
	}

	private String parseCDoc() {
		String line = parseBlockComment();
		String[] lines = line.split("\n");
		for (int x = 0; x < lines.length; x++) {
			lines[x] = lines[x].trim();
			if (lines[x].startsWith("*"))
				lines[x] = lines[x].substring(1).trim();
		}
		StringBuilder builder = new StringBuilder();
		for (String s : lines) {
			builder.append(s);
			builder.append('\n');
		}
		return builder.toString();
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
		} else if (peeks("/**")) {
			final String cdoc = parseCDoc();
			return new Token(this, pos, getPosition(), TokenType.C_DOC, cdoc);
		} else if (peeks("/*")) {
			parseBlockComment();
			return nextToken();
		} else if (peeks("(")) return new Token(this, pos, getPosition(), TokenType.LPAREN);
		else if (peeks(")")) return new Token(this, pos, getPosition(), TokenType.RPAREN);
		else if (peeks("[")) return new Token(this, pos, getPosition(), TokenType.LBRACK);
		else if (peeks("]")) return new Token(this, pos, getPosition(), TokenType.RBRACK);
		else if (peeks("{")) return new Token(this, pos, getPosition(), TokenType.LCURLY);
		else if (peeks("}")) return new Token(this, pos, getPosition(), TokenType.RCURLY);
		else if (peeks("...")) return new Token(this, pos, getPosition(), TokenType.ELLIPSIS);
		else if (peeks("=>")) return new Token(this, pos, getPosition(), TokenType.RDOUBLEARROW);
		else if (peeks("::")) return new Token(this, pos, getPosition(), TokenType.SCOPE);
		else if (peeks(".")) return new Token(this, pos, getPosition(), TokenType.DOT);
		else if (peeks(",")) return new Token(this, pos, getPosition(), TokenType.COMMA);
		else if (peeks(":")) return new Token(this, pos, getPosition(), TokenType.COLON);
		else if (peeks(";")) return new Token(this, pos, getPosition(), TokenType.SEMICOLON);
		else if (peeks("?")) return new Token(this, pos, getPosition(), TokenType.QUESTION);
		else if (peeks("->")) return new Token(this, pos, getPosition(), TokenType.RARROW);
		else if (peeks("++")) return new Token(this, pos, getPosition(), TokenType.INC1);
		else if (peeks("--")) return new Token(this, pos, getPosition(), TokenType.DEC1);
		else if (peeks("*")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.MULT, -1, 3, true));
		else if (peeks("/")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.DIV, -1, 3, true));
		else if (peeks("%")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.MOD, -1, 3, true));
		else if (peeks("+")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.PLUS, -1, 4, true));
		else if (peeks("-")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.MINUS, -1, 4, true));
		else if (peeks("<<")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.SHL, -1, 5, true));
		else if (peeks(">>")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.SHR, -1, 5, true));
		else if (peeks("<=")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.LEQ, -1, 6, true));
		else if (peeks(">=")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.GEQ, -1, 6, true));
		else if (peeks("<")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.LESS, -1, 6, true));
		else if (peeks(">")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.GREATER, -1, 6, true));
		else if (peeks("==")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.EQ, -1, 7, true));
		else if (peeks("!=")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.INEQ, -1, 7, true));
		else if (peeks("==")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.EQ, -1, 7, true));
		else if (peeks("&&")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.LOGAND, -1, 11, true));
		else if (peeks("||")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.LOGOR, -1, 12, true));
		else if (peeks("~")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.BITNOT, 1, -1, true));
		else if (peeks("&")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.BITAND, -1, 8, true));
		else if (peeks("^")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.BITXOR, -1, 9, true));
		else if (peeks("|")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.BITOR, -1, 10, true));
		else if (peeks("!")) return new Token(this, pos, getPosition(), TokenType.OPERATOR, new OperatorInfo(OperatorType.LOGNOT, 2, -1, true));
		else if (peeks("=")) return new Token(this, pos, getPosition(), TokenType.ASSIGNMENT);
		else if (peeks("\"")) {
			final String text = parseString('\"');
			return new Token(this, pos, getPosition(), TokenType.CONSTANT, new FString(text));
		}
		else if (peeks("\'")) {
			char c = (char) parseChar('\'');
			if (stream.next() != '\'')
				throw new ParseException(pos, "Expected \"\'\"!");
			return new Token(this, pos, getPosition(), TokenType.CONSTANT, FInteger.get(c));
		}
		
		
		StringBuilder builder = new StringBuilder();
		while (!isSpecial(stream.peek())) {
			builder.append((char) stream.next());
		}
		
		final String text = builder.toString();
		
		try {
			return new Token(this, pos, getPosition(), TokenType.CONSTANT, FInteger.get(Integer.decode(text)));
		} catch (NumberFormatException e) {
		}
		
		switch (text) {
		case "": return new Token(this, pos, getPosition(), TokenType.EOF);
		case "public": return new Token(this, pos, getPosition(), TokenType.PUBLIC);
		case "private": return new Token(this, pos, getPosition(), TokenType.PRIVATE);
		case "this": return new Token(this, pos, getPosition(), TokenType.THIS);
		case "nil": return new Token(this, pos, getPosition(), TokenType.NIL);
		case "true": return new Token(this, pos, getPosition(), TokenType.TRUE);
		case "false": return new Token(this, pos, getPosition(), TokenType.FALSE);
		case "use": return new Token(this, pos, getPosition(), TokenType.USE);
		case "inherits": return new Token(this, pos, getPosition(), TokenType.INHERITS);
		case "any": return new Token(this, pos, getPosition(), TokenType.ANY);
		case "void": return new Token(this, pos, getPosition(), TokenType.VOID);
		case "int": return new Token(this, pos, getPosition(), TokenType.INT);
		case "char": return new Token(this, pos, getPosition(), TokenType.CHAR);
		case "bool": return new Token(this, pos, getPosition(), TokenType.BOOL);
		case "string": return new Token(this, pos, getPosition(), TokenType.STRING);
		case "object": return new Token(this, pos, getPosition(), TokenType.OBJECT);
		case "func": return new Token(this, pos, getPosition(), TokenType.FUNC);
		case "list": return new Token(this, pos, getPosition(), TokenType.LIST);
		case "mapping": return new Token(this, pos, getPosition(), TokenType.MAPPING);
		case "struct": return new Token(this, pos, getPosition(), TokenType.STRUCT);
		case "class": return new Token(this, pos, getPosition(), TokenType.CLASS);
		case "new": return new Token(this, pos, getPosition(), TokenType.NEW);
		case "if": return new Token(this, pos, getPosition(), TokenType.IF);
		case "else": return new Token(this, pos, getPosition(), TokenType.ELSE);
		case "while": return new Token(this, pos, getPosition(), TokenType.WHILE);
		case "for": return new Token(this, pos, getPosition(), TokenType.FOR);
		case "foreach": return new Token(this, pos, getPosition(), TokenType.FOREACH);
		case "break": return new Token(this, pos, getPosition(), TokenType.BREAK);
		case "continue": return new Token(this, pos, getPosition(), TokenType.CONTINUE);
		case "return": return new Token(this, pos, getPosition(), TokenType.RETURN);
		case "va_next": return new Token(this, pos, getPosition(), TokenType.VA_NEXT);
		case "va_count": return new Token(this, pos, getPosition(), TokenType.VA_COUNT);
		default: return new Token(this, pos, getPosition(), TokenType.IDENT, text);
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
