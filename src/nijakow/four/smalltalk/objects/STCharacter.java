package nijakow.four.smalltalk.objects;

import nijakow.four.smalltalk.World;
import nijakow.four.smalltalk.vm.FourException;

public class STCharacter extends STInstance {
    private final char value;

    public STCharacter(char value) {
        this.value = value;
    }

    @Override
    public String toString() {
        switch (value)
        {
            case '\n': return "$\\n";
            case '\r': return "$\\r";
            case '\t': return "$\\t";
            case '\b': return "$\\b";
            case '\\': return "$\\\\";
            case '\033': return "$\\e";
            case ' ': return "$\\s";
            default:
                return "$" + value;
        }
    }

    @Override
    public void register() {
    }

    @Override
    public STCharacter asCharacter() throws FourException {
        return this;
    }

    @Override
    public boolean is(Object obj) {
        return (obj instanceof STCharacter) && getValue() == ((STCharacter) obj).getValue();
    }

    @Override
    public STClass getClass(World world) {
        return world.getCharacterClass();
    }

    public char getValue() {
        return this.value;
    }

    public static STCharacter get(char value) {
        return new STCharacter(value);
    }
}
