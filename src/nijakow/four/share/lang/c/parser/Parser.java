package nijakow.four.share.lang.c.parser;

import java.util.ArrayList;
import java.util.List;

import nijakow.four.share.lang.c.SlotVisibility;
import nijakow.four.share.lang.c.ast.*;
import nijakow.four.server.runtime.objects.standard.FInteger;
import nijakow.four.server.runtime.objects.Instance;
import nijakow.four.server.runtime.Key;
import nijakow.four.server.runtime.types.Type;
import nijakow.four.share.util.Pair;


public class Parser {
	private final Tokenizer tokenizer;
	private StreamPosition lastPos;

	private StreamPosition p() { return lastPos == null ? tokenizer.getPosition() : lastPos; }

	private void error(String message) throws ParseException {
		throw new ParseException(tokenizer.getPosition(), message);
	}
	
	private Type parseCoreType() throws ParseException {
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

	private Type parseType() throws ParseException {
		Type t = parseCoreType();
		if (t == null) return null;
		while (checkStar())
			t = t.listType();
		return t;
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
			return new ASTThis(p());
		} else if (check(TokenType.NIL)) {
			return new ASTConstant(p(), Instance.getNil());
		} else if (check(TokenType.TRUE)) {
			return new ASTConstant(p(), FInteger.getBoolean(true));
		} else if (check(TokenType.FALSE)) {
			return new ASTConstant(p(), FInteger.getBoolean(false));
		} else if (check(TokenType.VA_NEXT)) {
			return new ASTVaNext(p());
		} else if (check(TokenType.VA_COUNT)) {
			return new ASTVaCount(p());
		} else if (checkKeep(TokenType.CONSTANT)) {
			return new ASTConstant(p(), (Instance) nextToken().getPayload());
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
			return new ASTList(p(), Type.getList(), exprs.toArray(new ASTExpression[0]));
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
			return new ASTMapping(p(), exprs.toArray(new ASTExpression[0]));
		} else if (checkKeep(TokenType.IDENT)) {
			return new ASTIdent(p(), Key.get((String) nextToken().getPayload()));
		} else {
			return new ASTThis(p());
		}
	}
	
	private ASTExpression parseExpression(int prec) throws ParseException {
		ASTExpression expr;
		
		if (checkKeep(TokenType.OPERATOR)) {
			Token t = nextToken();
			OperatorInfo info = (OperatorInfo) t.getPayload();
			expr = new ASTUnaryOp(p(), info.getType(), parseExpression(info.getUnaryPrecedence()));
		} else if (check(TokenType.NEW)) {
			if (check(TokenType.LPAREN)) {
				StreamPosition pos = p();
				/*
				 * We do have cases where the "new" operator works like a function call:
				 *
				 *   object obj = new("/foo/bar.c", ...);
				 *
				 * In these cases, we invoke the routine "_new".
				 *                                           - nijakow
				 */
				Pair<ASTExpression[], Boolean> arglistData = parseArglist();
				expr = new ASTCall(pos, new ASTDot(null, new ASTThis(null), Key.get("_new")), arglistData.getFirst(), arglistData.getSecond());
			} else {
				Key clazz = expectKey();
				expect(TokenType.LPAREN);
				StreamPosition pos = p();
				Pair<ASTExpression[], Boolean> arglistData = parseArglist();
				expr = new ASTNew(pos, clazz, arglistData.getFirst(), arglistData.getSecond());
			}
		} else {
			expr = parseSimpleExpression(); 
		}
		
		ASTExpression next = null;
		while (expr != next) {
			next = expr;
			if (prec >= 13 && check(TokenType.QUESTION)) {
				ASTExpression consequent = parseExpression();
				expect(TokenType.COLON);
				ASTExpression alternative = parseExpression();
				expr = new ASTTernary(p(), expr, consequent, alternative);
			} else if (check(TokenType.DOT) || check(TokenType.RARROW)) {
				expr = new ASTDot(p(), expr, expectKey());
			} else if (check(TokenType.LPAREN)) {
				StreamPosition pos = p();
				Pair<ASTExpression[], Boolean> arglistData = parseArglist();
				expr = new ASTCall(pos, expr, arglistData.getFirst(), arglistData.getSecond());
			} else if (check(TokenType.LBRACK)) {
				expr = new ASTIndex(p(), expr, parseExpression());
				expect(TokenType.RBRACK);
			} else if (check(TokenType.SCOPE)) {
				ASTExpression[] args = parseOptionalExpressions();
				expr = new ASTScope(p(), expr, args, expectKey());
			} else if (check(TokenType.ASSIGNMENT)) {
				expr = new ASTAssignment(p(), expr, parseExpression());
			} else if (check(TokenType.INC1)) {
				expr = new ASTIncrement(p(), expr, 1, true);
			} else if (check(TokenType.DEC1)) {
				expr = new ASTIncrement(p(), expr, -1, true);
			} else if (checkKeep(TokenType.OPERATOR)) {
				Token t = nextToken();
				OperatorInfo info = (OperatorInfo) t.getPayload();
				if (info.getBinaryPrecedence() > prec || (info.isLeftToRight() && info.getBinaryPrecedence() == prec)) {
					t.fail();
				} else {
					expr = new ASTBinOp(p(), info.getType(), expr, parseExpression(info.getBinaryPrecedence()));
				}
			}
		}
		return expr;
	}
	
	private ASTExpression parseExpression() throws ParseException {
		return parseExpression(Integer.MAX_VALUE);
	}

	private ASTExpression[] parseOptionalExpressions() throws ParseException {
		List<ASTExpression> exprs = new ArrayList<>();
		if (check(TokenType.LPAREN)) {
			if (!check(TokenType.RPAREN)) {
				while (true) {
					exprs.add(parseExpression());
					if (check(TokenType.RPAREN)) break;
					expect(TokenType.COMMA);
				}
			}
		}
		return exprs.toArray(new ASTExpression[0]);
	}
	
	private ASTBlock parseBlock() throws ParseException {
		List<ASTInstruction> instructions = new ArrayList<>();
		StreamPosition pos = p();

		while (!check(TokenType.RCURLY)) {
			instructions.add(parseInstruction());
		}
		
		return new ASTBlock(pos, instructions.toArray(new ASTInstruction[0]));
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
			StreamPosition pos = p();
			Key name = expectKey();
			ASTExpression value = null;
			if (check(TokenType.ASSIGNMENT))
				value = parseExpression();
			expect(TokenType.SEMICOLON);
			return new ASTVarDecl(pos, type, name, value);
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
			StreamPosition pos = p();
			expect(TokenType.LPAREN);
			ASTExpression condition = parseExpression();
			expect(TokenType.RPAREN);
			ASTInstruction ifClause = parseInstruction();
			if (check(TokenType.ELSE)) {
				return new ASTIf(pos, condition, ifClause, parseInstruction());
			} else {
				return new ASTIf(pos, condition, ifClause);
			}
		} else if (check(TokenType.WHILE)) {
			StreamPosition pos = p();
			expect(TokenType.LPAREN);
			ASTExpression condition = parseExpression();
			expect(TokenType.RPAREN);
			return new ASTWhile(pos, condition, parseInstruction());
		} else if (check(TokenType.FOR)) {
			StreamPosition pos = p();
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
			return new ASTFor(p(), init, condition, update, parseInstruction());
		} else if (check(TokenType.FOREACH)) {
			StreamPosition pos = p();
			expect(TokenType.LPAREN);
			Pair<Type, Key> vt = parseVarAndType();
			if (vt == null || vt.getFirst() == null || vt.getSecond() == null)
				error("Expected a variable and a type!");
			expect(TokenType.COLON);
			ASTExpression init = parseExpression();
			expect(TokenType.RPAREN);
			ASTInstruction body = parseInstruction();
			return new ASTForeach(pos, vt.getFirst(), vt.getSecond(), init, body);
		} else if (check(TokenType.BREAK)) {
			StreamPosition pos = p();
			expect(TokenType.SEMICOLON);
			return new ASTBreak(pos);
		} else if (check(TokenType.CONTINUE)) {
			StreamPosition pos = p();
			expect(TokenType.SEMICOLON);
			return new ASTContinue(pos);
		} else if (check(TokenType.RETURN)) {
			StreamPosition pos = p();
			if (check(TokenType.SEMICOLON)) {
				return new ASTReturn(pos, null);
			} else {
				ASTExpression expr = parseExpression();
				expect(TokenType.SEMICOLON);
				return new ASTReturn(pos, expr);
			}
		} else {
			ASTExpression expr = parseExpression();
			expect(TokenType.SEMICOLON);
			return expr;
		}
	}
	
	private ASTClass parseClass(TokenType terminator) throws ParseException {
		List<ASTDecl> defs = new ArrayList<>();
		StreamPosition cpos = p();
	
		while (!check(terminator)) {
			if (check(TokenType.USE)) {
				StreamPosition pos = p();
				Key name = expectKey();
				defs.add(new ASTDefaultDef(pos, name));
				expect(TokenType.SEMICOLON);
			} else if (check(TokenType.INHERITS)) {
				StreamPosition pos = p();
				defs.add(new ASTInheritanceDef(pos, ((Instance) expect(TokenType.CONSTANT).getPayload()).asString()));
				expect(TokenType.SEMICOLON);
			} /*else if (check(TokenType.STRUCT) || check(TokenType.CLASS)) {
				StreamPosition pos = p();
				Key name = expectKey();
				expect(TokenType.LCURLY);
				defs.add(new ASTClassDef(pos, name, parseClass()));
				expect(TokenType.SEMICOLON);
			}*/ else {
				String cDoc = checkKeep(TokenType.C_DOC) ? (String) nextToken().getPayload() : "";

				SlotVisibility visibility = SlotVisibility.NONE;

				if (check(TokenType.PUBLIC)) {
					visibility = SlotVisibility.PUBLIC;
				} else if (check(TokenType.PRIVATE)) {
					visibility = SlotVisibility.PRIVATE;
				}

				Type type = parseType();
				Key name = expectKey();
				StreamPosition pos = p();
				if (check(TokenType.LPAREN)) {
					Pair<Pair<Type, Key>[], Boolean> args = parseArgdefs();
					expect(TokenType.LCURLY);
					ASTInstruction body = parseBlock();
					defs.add(new ASTFunctionDef(pos, cDoc, visibility, type, name, args.getFirst(), args.getSecond(), body));
				} else {
					defs.add(new ASTInstanceVarDef(pos, cDoc, visibility, type, name));
					expect(TokenType.SEMICOLON);
				}
			}
		}
		
		return new ASTClass(cpos, defs.toArray(new ASTDecl[0]));
	}

	private ASTClass parseClass() throws ParseException {
		return parseClass(TokenType.RCURLY);
	}

	public ASTClass parseFile() throws ParseException {
		return parseClass(TokenType.EOF);
	}

	public ASTExpression parseLine() throws ParseException {
		ASTExpression expr = parseExpression();
		check(TokenType.SEMICOLON);
		return expr;
	}

	private Token nextToken() throws ParseException {
		Token t = tokenizer.nextToken();
		this.lastPos = t.getPosition();
		return t;
	}
	
	private boolean checkKeep(TokenType type) throws ParseException {
		Token t = nextToken();
		t.fail();
		return t.is(type);
	}

	private boolean check(TokenType type) throws ParseException {
		Token t = nextToken();
		if (t.is(type)) return true;
		t.fail();
		return false;
	}

	private boolean checkStar() throws ParseException {
		Token t = nextToken();
		if (t.is(TokenType.OPERATOR) && ((OperatorInfo) t.getPayload()).getType() == OperatorType.MULT)
			return true;
		t.fail();
		return false;
	}
	
	private Token expect(TokenType type) throws ParseException {
		Token t = nextToken();
		if (t.is(type)) return t;
		else throw new ParseException(t, "Expected " + type + ", got " + t.getType() + "!");
	}
	
	private Key expectKey() throws ParseException {
		return Key.get((String) expect(TokenType.IDENT).getPayload());
	}
	
	public Parser(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}
}
