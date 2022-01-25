package nijakow.four.share.lang.c.parser;

import java.util.ArrayList;
import java.util.List;

import nijakow.four.share.lang.c.ast.*;
import nijakow.four.server.runtime.objects.FInteger;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.util.Pair;


public class Parser {
	private final Tokenizer tokenizer;

	private void error(String message) throws ParseException {
		throw new ParseException(tokenizer.getPosition(), message);
	}
	
	private Type parseType() throws ParseException {
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
			if (check(TokenType.LPAREN)) {
				Type t = parseType();
				if (t == null)
					error("Expected a type!");
				return t.listType();
			}
			return Type.getList();
		} else if (check(TokenType.MAPPING)) {
			return Type.getMapping();
		} else {
			return null;
		}
	}
	
	private Pair<Pair<Type, Key>[], Boolean> parseArgdefs() throws ParseException {
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
	
	private Pair<ASTExpression[], Boolean> parseArglist() throws ParseException {
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
	
	private ASTExpression parseSimpleExpression() throws ParseException {
		if (check(TokenType.THIS)) {
			return new ASTThis();
		} else if (check(TokenType.NIL)) {
			return new ASTConstant(Instance.getNil());
		} else if (check(TokenType.TRUE)) {
			return new ASTConstant(new FInteger(1));
		} else if (check(TokenType.FALSE)) {
			return new ASTConstant(new FInteger(0));
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
			return new ASTList(Type.getList(), exprs.toArray(new ASTExpression[0]));
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
	
	private ASTExpression parseExpression(int prec) throws ParseException {
		ASTExpression expr;
		
		if (checkKeep(TokenType.OPERATOR)) {
			Token t = tokenizer.nextToken();
			OperatorInfo info = (OperatorInfo) t.getPayload();
			expr = new ASTUnaryOp(info.getType(), parseExpression(info.getUnaryPrecedence()));
		} else if (check(TokenType.NEW)) {
			if (check(TokenType.LPAREN)) {
				/*
				 * We do have cases where the "new" operator works like a function call:
				 *
				 *   object obj = new("/foo/bar.c", ...);
				 *
				 * In these cases, we invoke the routine "_new".
				 *                                           - nijakow
				 */
				Pair<ASTExpression[], Boolean> arglistData = parseArglist();
				expr = new ASTCall(new ASTDot(new ASTThis(), Key.get("_new")), arglistData.getFirst(), arglistData.getSecond());
			} else {
				Key clazz = expectKey();
				expect(TokenType.LPAREN);
				Pair<ASTExpression[], Boolean> arglistData = parseArglist();
				expr = new ASTNew(clazz, arglistData.getFirst(), arglistData.getSecond());
			}
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
	
	private ASTExpression parseExpression() throws ParseException {
		return parseExpression(Integer.MAX_VALUE);
	}
	
	private ASTBlock parseBlock() throws ParseException {
		List<ASTInstruction> instructions = new ArrayList<>();
		
		while (!check(TokenType.RCURLY)) {
			instructions.add(parseInstruction());
		}
		
		return new ASTBlock(instructions.toArray(new ASTInstruction[0]));
	}

	private Pair<Type, Key> parseVarAndType() throws ParseException {
		Type type = parseType();
		if (type == null)
			return null;
		Key name = expectKey();
		return new Pair<>(type, name);
	}

	private ASTVarDecl parseVarDecl() throws ParseException {
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
	
	private ASTInstruction parseInstruction() throws ParseException {
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
		} else if (check(TokenType.FOREACH)) {
			expect(TokenType.LPAREN);
			Pair<Type, Key> vt = parseVarAndType();
			if (vt == null || vt.getFirst() == null || vt.getSecond() == null)
				error("Expected a variable and a type!");
			expect(TokenType.COLON);
			ASTExpression init = parseExpression();
			expect(TokenType.RPAREN);
			ASTInstruction body = parseInstruction();
			return new ASTForeach(vt.getFirst(), vt.getSecond(), init, body);
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
	
	private ASTClass parseClass(TokenType terminator) throws ParseException {
		List<ASTDecl> defs = new ArrayList<>();
	
		while (!check(terminator)) {
			if (check(TokenType.USE)) {
				Key name = expectKey();
				defs.add(new ASTDefaultDef(name));
				expect(TokenType.SEMICOLON);
			} else if (check(TokenType.INHERITS)) {
				defs.add(new ASTInheritanceDef(((Instance) expect(TokenType.CONSTANT).getPayload()).asString()));
				expect(TokenType.SEMICOLON);
			} else if (check(TokenType.STRUCT) || check(TokenType.CLASS)) {
				Key name = expectKey();
				expect(TokenType.LCURLY);
				defs.add(new ASTClassDef(name, parseClass()));
				expect(TokenType.SEMICOLON);
			} else {
				Type type = parseType();
				Key name = expectKey();
				if (check(TokenType.LPAREN)) {
					Pair<Pair<Type, Key>[], Boolean> args = parseArgdefs();
					expect(TokenType.LCURLY);
					ASTInstruction body = parseBlock();
					defs.add(new ASTFunctionDef(type, name, args.getFirst(), args.getSecond(), body));
				} else {
					defs.add(new ASTInstanceVarDef(type, name));
					expect(TokenType.SEMICOLON);
				}
			}
		}
		
		return new ASTClass(defs.toArray(new ASTDecl[0]));
	}

	private ASTClass parseClass() throws ParseException {
		return parseClass(TokenType.RCURLY);
	}

	public ASTClass parseFile() throws ParseException {
		return parseClass(TokenType.EOF);
	}
	
	private boolean checkKeep(TokenType type) throws ParseException {
		Token t = tokenizer.nextToken();
		t.fail();
		return t.is(type);
	}
	
	private boolean check(TokenType type) throws ParseException {
		Token t = tokenizer.nextToken();
		if (t.is(type)) return true;
		t.fail();
		return false;
	}
	
	private Token expect(TokenType type) throws ParseException {
		Token t = tokenizer.nextToken();
		if (t.is(type)) return t;
		else throw new ParseException(t, "Expected different token! " + type + ", got " + t.getType());
	}
	
	private Key expectKey() throws ParseException {
		return Key.get((String) expect(TokenType.IDENT).getPayload());
	}
	
	public Parser(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
}