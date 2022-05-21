package nijakow.four.smalltalk.parser;

import nijakow.four.share.util.Pair;
import nijakow.four.smalltalk.ast.*;
import nijakow.four.smalltalk.objects.STSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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

    private boolean checkUnary() { return false; }
    private boolean checkBinary() { return false; }
    private boolean checkNAry() { return false; }

    private String getSymbolName() { return ""; }

    private STSymbol expectSymbol() {
        return null;
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
            // TODO
        }
    }

    private <T> Pair<STSymbol, T[]> parseSmalltalkOp(Supplier<T> subparser, int level) {
        StringBuilder name = new StringBuilder();
        List<T> ts = new ArrayList<>();

        if (checkUnary()) {
            name.append(getSymbolName());
            advance();
        } else if (checkBinary()) {
            name.append(getSymbolName());
            advance();
            ts.add(subparser.get());
        } else {
            if (!checkNAry())
                return null;
            while (checkNAry()) {
                name.append(getSymbolName());
                advance();
                subparser.get();
            }
        }
        return new Pair<>(STSymbol.get(name.toString()), (T[]) ts.toArray());
    }

    private <T> Pair<STSymbol, T[]> parseSmalltalkOp(Supplier<T> subparser) {
        return parseSmalltalkOp(subparser, 3);
    }

    private ExprAST parseSend(ExprAST receiver) {
        Pair<STSymbol, ExprAST[]> pair = parseSmalltalkOp(() -> parseExpression());
        if (pair == null)
            return receiver;
        return new SendAST(receiver, pair.getFirst(), pair.getSecond());
    }

    private ExprAST parseSimpleExpression() {
        if (check(TokenType.LPAREN)) {
            ExprAST ast = parseExpression();
            expect(TokenType.RPAREN);
            return ast;
        } else if (is(TokenType.SYMBOL)) {
            SymbolAST ast = new SymbolAST(STSymbol.get(getSymbolName()));
            advance();
            return ast;
        } else {
            expect(TokenType.SELF);
            return new SelfAST();
        }
    }

    private ExprAST parseExpression() {
        ExprAST expr = parseSimpleExpression();
        ExprAST next = expr;

        while (expr != next || check(TokenType.SEMICOLON)) {
            next = expr;
            expr = parseSend(expr);
        }

        return expr;
    }

    private ExprsAST parseExpressionsUntil(TokenType type) {
        List<ExprAST> expressions = new ArrayList<>();
        if (!check(type)) {
            while (true) {
                expressions.add(parseExpression());
                if (check(type))
                    break;
                expect(TokenType.DOT);
            }
        }
        return new ExprsAST(expressions.toArray(new ExprAST[]{}));
    }

    public MethodAST parseMethod() {
        Pair<STSymbol, STSymbol[]> head = parseSmalltalkOp(() -> expectSymbol());
        List<STSymbol> locals = new ArrayList<>();
        if (check(TokenType.BAR)) {
            while (is(TokenType.SYMBOL)) {
                locals.add(STSymbol.get(getSymbolName()));
                advance();
            }
        }
        expect(TokenType.LBRACK);
        return new MethodAST(head.getFirst(), head.getSecond(), new BlockAST(locals.toArray(new STSymbol[]{}), parseExpressionsUntil(TokenType.RBRACK)));
    }

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }
}
