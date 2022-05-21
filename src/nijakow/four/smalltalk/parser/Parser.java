package nijakow.four.smalltalk.parser;

import nijakow.four.share.util.Pair;
import nijakow.four.smalltalk.ast.ExprAST;
import nijakow.four.smalltalk.ast.ExprsAST;
import nijakow.four.smalltalk.ast.BlockAST;
import nijakow.four.smalltalk.ast.MethodAST;
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

    private <T> Pair<STSymbol, T[]> parseSmalltalkOp(Supplier<T> subparser) {
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
            while (checkNAry()) {
                name.append(getSymbolName());
                advance();
                subparser.get();
            }
        }
        return new Pair<>(STSymbol.get(name.toString()), (T[]) ts.toArray());
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

    private MethodAST parseMethod() {
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
