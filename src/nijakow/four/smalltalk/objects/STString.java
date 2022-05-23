package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.objects.method.STCompiledMethod;
import nijakow.four.smalltalk.parser.Parser;
import nijakow.four.smalltalk.parser.StringCharacterStream;
import nijakow.four.smalltalk.parser.Tokenizer;
import nijakow.four.smalltalk.vm.FourException;

public class STString extends STInstance {
    private final String value;

    public STString(String value) {
        this.value = value;
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

    public STCompiledMethod compile() {
        StringCharacterStream stream = new StringCharacterStream(getValue());
        Tokenizer tokenizer = new Tokenizer(stream);
        Parser parser = new Parser(tokenizer);
        return parser.parseCL().compile(getValue());
    }
}
