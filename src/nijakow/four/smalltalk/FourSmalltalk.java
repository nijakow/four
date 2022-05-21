package nijakow.four.smalltalk;

import nijakow.four.smalltalk.ast.MethodAST;
import nijakow.four.smalltalk.parser.Parser;
import nijakow.four.smalltalk.parser.StringCharacterStream;
import nijakow.four.smalltalk.parser.Tokenizer;

public class FourSmalltalk {

    public static void main(String[] args) {
        StringCharacterStream cs = new StringCharacterStream("foo\n[ self ]");
        Tokenizer tokenizer = new Tokenizer(cs);
        Parser parser = new Parser(tokenizer);
        MethodAST method = parser.parseMethod();
        System.out.println(method);
    }
}
