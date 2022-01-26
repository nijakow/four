package nijakow.four.server.runtime;

import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.server.runtime.objects.Blueprint;

public interface FourClassLoader {
    Blueprint load(String path) throws ParseException, CompilationException;
    Blueprint load(Key name);
}
