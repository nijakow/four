package nijakow.four.server.logging;

import nijakow.four.share.lang.FourCompilerException;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;

public class CompilationLogger {
    private final Logger logger;
    private final StringBuilder transcript = new StringBuilder();

    public CompilationLogger(Logger logger) {
        this.logger = logger;
    }

    public void visitFile(String path) {
        this.logger.println(LogLevel.INFO, "Compiling " + path + "...");
    }

    public void tell(FourCompilerException e) {
        this.logger.printException(e);
        transcript.append(e.getErrorText());
    }

    public String getTranscript() {
        return transcript.toString();
    }
}
