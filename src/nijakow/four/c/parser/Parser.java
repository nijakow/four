package nijakow.four.c.parser;

import java.util.ArrayList;
import java.util.List;

import nijakow.four.c.ast.*;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.Type;
import nijakow.four.util.Pair;


public class Parser {
	private Tokenizer tokenizer;

	private Type parseType() {
		if (check(TokenType.ANY)) {
			return new Type(); // TODO
		} else {
			return null;
		}
	}
	
	private Pair<Type, Key>[] parseArgdefs() {
		List<Pair<Type, Key>> args = new ArrayList<>();
		
		if (!check(TokenType.RPAREN)) {
			while (true) {
				Type t = parseType();
				Key k = expectKey();
				args.add(new Pair<>(t, k));
				if (check(TokenType.RPAREN))
					break;
				expect(TokenType.COMMA);
			}
		}
		
		return (Pair<Type, Key>[]) args.toArray(new Pair<>[0]);
	}
	
	private ASTExpression[] parseArglist() {
		List<ASTExpression> args = new ArrayList<>();
		
		if (!check(TokenType.RPAREN)) {
			while (true) {
				args.add(parseExpression());
				if (check(TokenType.RPAREN))
					break;
				expect(TokenType.COMMA);
			}
		}
		
		return args.toArray(new ASTExpression[0]);
	}
	
	private ASTExpression parseSimpleExpression() {
		if (check(TokenType.THIS)) {
			return new ASTThis();
		} else if (checkKeep(TokenType.CONSTANT)) {
			return new ASTConstant((Instance) tokenizer.nextToken().getPayload());
		} else if (check(TokenType.LPAREN)) {
			ASTExpression expr = parseExpression();
			expect(TokenType.RPAREN);
			return expr;
		}
		return new ASTIdent(expectKey());
	}
	
	private ASTExpression parseExpression(int prec) {
		ASTExpression expr = parseSimpleExpression();
		ASTExpression next = null;
		while (expr != next) {
			next = expr;
			if (check(TokenType.DOT) || check(TokenType.RARROW)) {
				expr = new ASTDot(expr, expectKey());
			} else if (check(TokenType.LPAREN)) {
				expr = new ASTCall(expr, parseArglist());
			} else if (check(TokenType.ASSIGNMENT)) {
				expr = new ASTAssignment(expr, parseExpression());
			} else if (checkKeep(TokenType.OPERATOR)) {
				Token t = tokenizer.nextToken();
				OperatorInfo info = (OperatorInfo) t.getPayload();
				if (info.getBinaryPrecedence() > prec || (info.isLeftToRight() && info.getBinaryPrecedence() == prec)) {
					t.fail();
				} else {
					expr = new ASTBinOp(info.getType(), expr, parseExpression(info.getBinaryPrecedence()));
				}
			}
		}
		return expr;
	}
	
	private ASTExpression parseExpression() {
		return parseExpression(Integer.MAX_VALUE);
	}
	
	private ASTBlock parseBlock() {
		List<ASTInstruction> instructions = new ArrayList<>();
		
		while (!check(TokenType.RCURLY)) {
			instructions.add(parseInstruction());
		}
		
		return new ASTBlock(instructions.toArray(new ASTInstruction[0]));
	}
	
	private ASTInstruction parseInstruction() {
		Type type = parseType();
		if (type != null) {
			Key name = expectKey();
			expect(TokenType.SEMICOLON);
			return new ASTVarDecl(type, name);
		} else if (check(TokenType.LCURLY)) {
			return parseBlock();
		} else if (check(TokenType.IF)) {
			expect(TokenType.LPAREN);
			ASTExpression condition = parseExpression();
			expect(TokenType.RPAREN);
			ASTInstruction ifClause = parseInstruction();
			if (check(TokenType.ELSE)) {
				return new ASTIf(condition, ifClause, parseInstruction());
			} else {
				return new ASTIf(condition, ifClause);
			}
		} else if (check(TokenType.WHILE)) {
			expect(TokenType.LPAREN);
			ASTExpression condition = parseExpression();
			expect(TokenType.RPAREN);
			return new ASTWhile(condition, parseInstruction());
		} else if (check(TokenType.FOR)) {
			expect(TokenType.LPAREN);
			ASTExpression init = parseExpression();
			expect(TokenType.SEMICOLON);
			ASTExpression condition = parseExpression();
			expect(TokenType.SEMICOLON);
			ASTExpression update= parseExpression();
			expect(TokenType.RPAREN);
			return new ASTFor(init, condition, update, parseInstruction());
		} else if (check(TokenType.RETURN)) {
			if (check(TokenType.SEMICOLON)) {
				return new ASTReturn(new ASTThis());
			} else {
				ASTExpression expr = parseExpression();
				expect(TokenType.SEMICOLON);
				return new ASTReturn(expr);
			}
		} else {
			ASTExpression expr = parseExpression();
			expect(TokenType.SEMICOLON);
			return expr;
		}
	}
	
	public ASTFile parse() {
		List<ASTDefinition> defs = new ArrayList<>();
	
		while (!check(TokenType.EOF)) {
			Type type = parseType();
			Key name = expectKey();
			if (check(TokenType.LPAREN)) {
				Pair<Type, Key>[] args = parseArgdefs();
				ASTInstruction body = parseInstruction();
				defs.add(new ASTFunctionDef(type, name, args, body));
			} else {
				defs.add(new ASTInstanceVarDef(type, name));
				expect(TokenType.SEMICOLON);
			}
		}
		
		return new ASTFile(defs.toArray(new ASTDefinition[0]));
	}
	
	private boolean checkKeep(TokenType type) {
		Token t = tokenizer.nextToken();
		t.fail();
		return t.is(type);
	}
	
	private boolean check(TokenType type) {
		Token t = tokenizer.nextToken();
		if (t.is(type)) return true;
		t.fail();
		return false;
	}
	
	private Token expect(TokenType type) {
		Token t = tokenizer.nextToken();
		if (t.is(type)) return t;
		else throw new RuntimeException("Expected different token!");
	}
	
	private Key expectKey() {
		return Key.get((String) expect(TokenType.IDENT).getPayload());
	}
	
	public Parser(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
}
