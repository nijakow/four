package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.parser.ParseException;
import nijakow.four.smalltalk.parser.Parser;
import nijakow.four.smalltalk.parser.StringCharacterStream;
import nijakow.four.smalltalk.parser.Tokenizer;
import nijakow.four.smalltalk.vm.FourException;

public class STString extends STInstance {
    private final String value;

    public STString(String value) {
        this.value = value;
    }

    public static STInstance wrap(String value) {
        if (value == null) return STNil.get();
        else               return new STString(value);
    }

    @Override
    public String toString() {
        return "'" + getValue() + "'";
    }

    @Override
    public STString asString() throws FourException {
        return this;
    }

    @Override
    public boolean is(Object obj) {
        return (obj instanceof STString) && getValue().equals(((STString) obj).getValue());
    }

    @Override
    public STClass getClass(World world) {
        return world.getStringClass();
    }

    public String getValue() {
        return this.value;
    }

    public STCompiledMethod compile() throws ParseException {
        StringCharacterStream stream = new StringCharacterStream(getValue());
        Tokenizer tokenizer = new Tokenizer(stream);
        Parser parser = new Parser(tokenizer);
        return parser.parseCL().compile(getValue());
    }
}
