package nijakow.four.server.runtime;

import nijakow.four.share.lang.c.compiler.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.runtime.objects.Blueprint;

public interface FourClassLoader {
    Blueprint load(String name) throws ParseException, CompilationException;
}
