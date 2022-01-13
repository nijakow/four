package nijakow.four.runtime;

import nijakow.four.c.compiler.CompilationException;
import nijakow.four.c.parser.ParseException;

public interface FourClassLoader {
    Blueprint load(String name) throws ParseException, CompilationException;
}
