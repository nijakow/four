package nijakow.four.runtime;

import nijakow.four.lang.c.compiler.CompilationException;
import nijakow.four.lang.c.parser.ParseException;
import nijakow.four.runtime.objects.Blueprint;

public interface FourClassLoader {
    Blueprint load(String name) throws ParseException, CompilationException;
}
