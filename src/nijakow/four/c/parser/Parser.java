package nijakow.four.c.parser;

import java.util.ArrayList;
import java.util.List;

import nijakow.four.c.ast.ASTAssignment;
import nijakow.four.c.ast.ASTBinOp;
import nijakow.four.c.ast.ASTBlock;
import nijakow.four.c.ast.ASTBreak;
import nijakow.four.c.ast.ASTCall;
import nijakow.four.c.ast.ASTConstant;
import nijakow.four.c.ast.ASTContinue;
import nijakow.four.c.ast.ASTDecl;
import nijakow.four.c.ast.ASTDefaultDef;
import nijakow.four.c.ast.ASTDot;
import nijakow.four.c.ast.ASTExpression;
import nijakow.four.c.ast.ASTFile;
import nijakow.four.c.ast.ASTFor;
import nijakow.four.c.ast.ASTFunctionDef;
import nijakow.four.c.ast.ASTIdent;
import nijakow.four.c.ast.ASTIf;
import nijakow.four.c.ast.ASTIncrement;
import nijakow.four.c.ast.ASTIndex;
import nijakow.four.c.ast.ASTInheritanceDef;
import nijakow.four.c.ast.ASTInstanceVarDef;
import nijakow.four.c.ast.ASTInstruction;
import nijakow.four.c.ast.ASTList;
import nijakow.four.c.ast.ASTMapping;
import nijakow.four.c.ast.ASTReturn;
import nijakow.four.c.ast.ASTScope;
import nijakow.four.c.ast.ASTThis;
import nijakow.four.c.ast.ASTUnaryOp;
import nijakow.four.c.ast.ASTVaCount;
import nijakow.four.c.ast.ASTVaNext;
import nijakow.four.c.ast.ASTVarDecl;
import nijakow.four.c.ast.ASTWhile;
import nijakow.four.c.runtime.Instance;
import nijakow.four.c.runtime.Key;
import nijakow.four.c.runtime.Type;
import nijakow.four.util.Pair;


public class Parser {
	private Tokenizer tokenizer;

	private Type parseType() {
		/*
		 * TODO: More types
		 */
		if (check(TokenType.ANY)) {
			return Type.getAny();
		} else if (check(TokenType.VOID)) {
			return Type.getVoid();
		} else if (check(TokenType.INT)) {
			return Type.getInt();
		} else if (check(TokenType.CHAR)) {
			return Type.getChar();
		} else if (check(TokenType.BOOL)) {
			return Type.getBool();
		} else if (check(TokenType.STRING)) {
			return Type.getString();
		} else if (check(TokenType.OBJECT)) {
			return Type.getObject();
		} else if (check(TokenType.FUNC)) {
			return Type.getFunc();
		} else if (check(TokenType.LIST)) {
			return Type.getList();
		} else if (check(TokenType.MAPPING)) {
			return Type.getMapping();
		} else {
			return null;
		}
	}
	
	private Pair<Pair<Type, Key>[], Boolean> parseArgdefs() {
		List<Pair<Type, Key>> args = new ArrayList<>();
		boolean hasVarargs = false;
		
		if (!check(TokenType.RPAREN)) {
			while (true) {
				if (check(TokenType.ELLIPSIS)) {
					hasVarargs = true;
					expect(TokenType.RPAREN);
					break;
				}
				Type t = parseType();
				Key k = expectKey();
				args.add(new Pair<>(t, k));
				if (check(TokenType.RPAREN))
					break;
				expect(TokenType.COMMA);
			}
		}
		
		return new Pair<>((Pair<Type, Key>[]) args.toArray(new Pair[0]), hasVarargs);
	}
	
	private Pair<ASTExpression[], Boolean> parseArglist() {
		List<ASTExpression> args = new ArrayList<>();
		boolean hasVarargs = false;
		
		if (!check(TokenType.RPAREN)) {
			while (true) {
				if (check(TokenType.ELLIPSIS)) {
					hasVarargs = true;
					expect(TokenType.RPAREN);
					break;
				}
				args.add(parseExpression());
				if (check(TokenType.RPAREN))
					break;
				expect(TokenType.COMMA);
			}
		}
		
		return new Pair<>(args.toArray(new ASTExpression[0]), hasVarargs);
	}
	
	private ASTExpression parseSimpleExpression() {
		if (check(TokenType.THIS)) {
			return new ASTThis();
		} else if (check(TokenType.NIL)) {
			return new ASTConstant(Instance.getNil());
		} else if (check(TokenType.VA_NEXT)) {
			return new ASTVaNext();
		} else if (check(TokenType.VA_COUNT)) {
			return new ASTVaCount();
		} else if (checkKeep(TokenType.CONSTANT)) {
			return new ASTConstant((Instance) tokenizer.nextToken().getPayload());
		} else if (check(TokenType.LPAREN)) {
			ASTExpression expr = parseExpression();
			expect(TokenType.RPAREN);
			return expr;
		} else if (check(TokenType.LCURLY)) {
			List<ASTExpression> exprs = new ArrayList<>();
			if (!check(TokenType.RCURLY)) {
				while (true) {
					exprs.add(parseExpression());
					if (check(TokenType.RCURLY))
						break;
					expect(TokenType.COMMA);
				}
			}
			return new ASTList(exprs.toArray(new ASTExpression[0]));
		} else if (check(TokenType.LBRACK)) {
			List<ASTExpression> exprs = new ArrayList<>();
			if (!check(TokenType.RBRACK)) {
				while (true) {
					exprs.add(parseExpression());
					if (check(TokenType.RBRACK))
						break;
					if (!check(TokenType.COLON))
						expect(TokenType.COMMA);
				}
			}
			return new ASTMapping(exprs.toArray(new ASTExpression[0]));
		} else if (checkKeep(TokenType.IDENT)) {
			return new ASTIdent(Key.get((String) tokenizer.nextToken().getPayload()));
		} else {
			return new ASTThis();
		}
	}
	
	private ASTExpression parseExpression(int prec) {
		ASTExpression expr;
		
		if (checkKeep(TokenType.OPERATOR)) {
			Token t = tokenizer.nextToken();
			OperatorInfo info = (OperatorInfo) t.getPayload();
			expr = new ASTUnaryOp(info.getType(), parseExpression(info.getUnaryPrecedence()));
		} else {
			expr = parseSimpleExpression(); 
		}
		
		ASTExpression next = null;
		while (expr != next) {
			next = expr;
			if (check(TokenType.DOT) || check(TokenType.RARROW)) {
				expr = new ASTDot(expr, expectKey());
			} else if (check(TokenType.LPAREN)) {
				Pair<ASTExpression[], Boolean> arglistData = parseArglist();
				expr = new ASTCall(expr, arglistData.getFirst(), arglistData.getSecond());
			} else if (check(TokenType.LBRACK)) {
				expr = new ASTIndex(expr, parseExpression());
				expect(TokenType.RBRACK);
			} else if (check(TokenType.SCOPE)) {
				expr = new ASTScope(expr, expectKey());
			} else if (check(TokenType.ASSIGNMENT)) {
				expr = new ASTAssignment(expr, parseExpression());
			} else if (check(TokenType.INC1)) {
				expr = new ASTIncrement(expr, 1, true);
			} else if (check(TokenType.DEC1)) {
				expr = new ASTIncrement(expr, -1, true);
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
	
	private ASTVarDecl parseVarDecl() {
		Type type = parseType();
		if (type != null) {
			Key name = expectKey();
			ASTExpression value = null;
			if (check(TokenType.ASSIGNMENT))
				value = parseExpression();
			expect(TokenType.SEMICOLON);
			return new ASTVarDecl(type, name, value);
		} else {
			return null;
		}
	}
	
	private ASTInstruction parseInstruction() {
		ASTVarDecl decl = parseVarDecl();
		if (decl != null) {
			return decl;
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
			ASTInstruction init = parseVarDecl();
			if (init == null) {
				init = parseExpression();
				expect(TokenType.SEMICOLON);
			}
			ASTExpression condition = parseExpression();
			expect(TokenType.SEMICOLON);
			ASTExpression update = parseExpression();
			expect(TokenType.RPAREN);
			return new ASTFor(init, condition, update, parseInstruction());
		} else if (check(TokenType.BREAK)) {
			expect(TokenType.SEMICOLON);
			return new ASTBreak();
		} else if (check(TokenType.CONTINUE)) {
			expect(TokenType.SEMICOLON);
			return new ASTContinue();	
		} else if (check(TokenType.RETURN)) {
			if (check(TokenType.SEMICOLON)) {
				return new ASTReturn(null);
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
		List<ASTDecl> defs = new ArrayList<>();
	
		while (!check(TokenType.EOF)) {
			if (check(TokenType.USE)) {
				Key name = expectKey();
				defs.add(new ASTDefaultDef(name));
				expect(TokenType.SEMICOLON);
			} else if (check(TokenType.INHERIT)) {
				defs.add(new ASTInheritanceDef(((Instance) expect(TokenType.CONSTANT).getPayload()).asString()));
				expect(TokenType.SEMICOLON);
			} else {
				Type type = parseType();
				Key name = expectKey();
				if (check(TokenType.LPAREN)) {
					Pair<Pair<Type, Key>[], Boolean> args = parseArgdefs();
					ASTInstruction body = parseInstruction();
					defs.add(new ASTFunctionDef(type, name, args.getFirst(), args.getSecond(), body));
				} else {
					defs.add(new ASTInstanceVarDef(type, name));
					expect(TokenType.SEMICOLON);
				}
			}
		}
		
		return new ASTFile(defs.toArray(new ASTDecl[0]));
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
		else throw new RuntimeException("Expected different token! " + type + ", got " + t.getType());
	}
	
	private Key expectKey() {
		return Key.get((String) expect(TokenType.IDENT).getPayload());
	}
	
	public Parser(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
}
