package nijakow.four.smalltalk.parser;

import nijakow.four.share.util.Pair;
import nijakow.four.smalltalk.ast.*;
import nijakow.four.smalltalk.objects.STInteger;
import nijakow.four.smalltalk.objects.STString;
import nijakow.four.smalltalk.objects.STSymbol;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Tokenizer tokenizer;
    private Token current;

    private Token current() {
        if (current == null)
            current = tokenizer.nextToken();
        return current;
    }

    private void advance() {
        if (current == null)
            current = tokenizer.nextToken();
        else
            current = current.next();
    }

    private boolean isUnary() { return is(TokenType.IDENTIFIER) && !isBinary() && !isNAry(); }
    private boolean isBinary() {
        String name = getSymbolName();
        if (name == null) return false;
        char c = name.charAt(name.length() - 1);
        return c != ':' && !(Character.isAlphabetic(c) || Character.isDigit(c));
    }
    private boolean isNAry() {
        String name = getSymbolName();
        return name != null && name.charAt(name.length() - 1) == ':';
    }

    private String getSymbolName() {
        if (!is(TokenType.IDENTIFIER))
            return null;
        return ((STSymbol) current().getPayload()).getName();
    }

    private STSymbol expectSymbol() {
        Token t = current();
        expect(TokenType.IDENTIFIER);
        return (STSymbol) t.getPayload();
    }

    private boolean is(TokenType type) {
        return current().getType() == type;
    }

    private boolean check(TokenType type) {
        if (is(type)) {
            advance();
            return true;
        }
        return false;
    }

    private void expect(TokenType type) {
        if (!check(type)) {
            throw new RuntimeException("Expected " + type + ", got " + current().getType());
        }
    }

    private Pair<STSymbol, STSymbol[]> parseSmalltalkArglist() {
        StringBuilder name = new StringBuilder();
        List<STSymbol> ts = new ArrayList<>();

        if (isUnary()) {
            name.append(getSymbolName());
            advance();
        } else if (isBinary()) {
            name.append(getSymbolName());
            advance();
            ts.add(expectSymbol());
        } else {
            if (!isNAry())
                return null;
            while (isNAry()) {
                name.append(getSymbolName());
                advance();
                ts.add(expectSymbol());
            }
        }
        return new Pair<>(STSymbol.get(name.toString()), ts.toArray(new STSymbol[]{}));
    }

    private ExprAST parseSend(ExprAST receiver, int prio) {
        if (isUnary()) {
            return new SendAST(receiver, expectSymbol(), new ExprAST[]{});
        } else if (prio > 0 && isBinary()) {
            return new SendAST(receiver, expectSymbol(), new ExprAST[]{ parseExpression(0) });
        } else if (prio > 1 && isNAry()) {
            StringBuilder name = new StringBuilder();
            List<ExprAST> ts = new ArrayList<>();
            while (isNAry()) {
                name.append(getSymbolName());
                advance();
                ts.add(parseExpression(1));
            }
            return new SendAST(receiver, STSymbol.get(name.toString()), ts.toArray(new ExprAST[]{}));
        }
        return receiver;
    }

    private BlockAST parseBlock() {
        List<STSymbol> ts = new ArrayList<>();

        while (check(TokenType.COLON))
            ts.add(expectSymbol());
        if (!ts.isEmpty())
            expect(TokenType.BAR);

        return new BlockAST(ts.toArray(new STSymbol[]{}), parseExpressionsUntil(TokenType.RBRACK));
    }

    private ExprAST parseSimpleExpression(int prio) {
        if (check(TokenType.LPAREN)) {
            ExprAST ast = parseExpression();
            expect(TokenType.RPAREN);
            return ast;
        } else if (is(TokenType.IDENTIFIER)) {
            STSymbol symbol = STSymbol.get(getSymbolName());
            advance();
            if (check(TokenType.ASSIGN)) {
                return new AssignAST(symbol, parseExpression(prio));
            } else {
                return new SymbolAST(symbol);
            }
        } else if (is(TokenType.INTEGER)) {
            STInteger integer = STInteger.get((Integer) current().getPayload());
            advance();
            return new ConstantAST(integer);
        } else if (is(TokenType.STRING)) {
            STString string = new STString((String) current().getPayload());
            advance();
            return new ConstantAST(string);
        } else if (is(TokenType.SYMBOL)) {
            STSymbol symbol = (STSymbol) current().getPayload();
            advance();
            return new ConstantAST(symbol);
        } else if (check(TokenType.LBRACK)) {
            return parseBlock();
        } else if (check(TokenType.CARET)) {
            return new ReturnAST(parseExpression(prio));
        } else {
            expect(TokenType.SELF);
            return new SelfAST();
        }
    }

    private ExprAST parseExpression(int prio) {
        ExprAST expr = parseSimpleExpression(prio);
        ExprAST next = null;

        while (expr != next || (prio >= 2 && check(TokenType.SEMICOLON))) {
            next = expr;
            expr = parseSend(expr, prio);
        }

        return expr;
    }

    private ExprAST parseExpression() {
        return parseExpression(2);
    }

    private ExprsAST parseExpressionsUntil(TokenType type) {
        List<ExprAST> expressions = new ArrayList<>();
        if (!check(type)) {
            while (true) {
                expressions.add(parseExpression());
                if (check(type))
                    break;
                expect(TokenType.DOT);
                if (check(type))
                    break;
            }
        }
        return new ExprsAST(expressions.toArray(new ExprAST[]{}));
    }

    public MethodAST parseMethod() {
        Pair<STSymbol, STSymbol[]> head = parseSmalltalkArglist();
        List<STSymbol> locals = new ArrayList<>();
        if (check(TokenType.BAR)) {
            while (is(TokenType.IDENTIFIER)) {
                locals.add(STSymbol.get(getSymbolName()));
                advance();
            }
        }
        expect(TokenType.LBRACK);
        return new MethodAST(head.getFirst(), head.getSecond(), locals.toArray(new STSymbol[]{}), parseExpressionsUntil(TokenType.RBRACK));
    }

    public CommandLineAST parseCL() {
        List<STSymbol> locals = new ArrayList<>();
        if (check(TokenType.BAR)) {
            while (is(TokenType.IDENTIFIER)) {
                locals.add(STSymbol.get(getSymbolName()));
                advance();
            }
            expect(TokenType.BAR);
        }
        return new CommandLineAST(locals.toArray(new STSymbol[]{}), parseExpression());
    }

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }
}
