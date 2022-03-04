package nijakow.four.server.logging;

import nijakow.four.share.lang.c.parser.ParseException;
import nijakow.four.share.util.Pair;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    private List<Pair<Instant, String>> logHistory = new ArrayList<>();
    private Instant startOfLine = null;
    private StringBuilder line = new StringBuilder();

    private Instant getNow() {
        return Instant.now();
    }

    private String formatLine(Instant i, String l) {
        return String.format("%12s %s", "[" + i.toString() + "]:", l);
    }

    private void notifyNewLine(Instant now, String line) {
        logHistory.add(new Pair(now, line));
        System.out.println(formatLine(now, line));
    }

    public void print(LogLevel l, char c) {
        if (startOfLine == null)
            startOfLine = getNow();
        if (c == '\n') {
            notifyNewLine(startOfLine, line.toString());
            startOfLine = null;
            line = new StringBuilder();
        } else {
            line.append(c);
        }
    }

    public void print(LogLevel l, String s) {
        for (int i = 0; i < s.length(); i++)
            print(l, s.charAt(i));
    }

    public void println(LogLevel l, String line) {
        print(l, line);
        print(l, '\n');
    }

    public void printException(LogLevel l, Throwable e) {
        StringWriter writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        e.printStackTrace(pw);
        pw.close();
        print(l, writer.getBuffer().toString());
    }

    public void printException(Throwable e) {
        printException(LogLevel.ERROR, e);
    }

    public CompilationLogger newCompilationLogger() {
        return new CompilationLogger(this);
    }

    public String[] getLines() {
        List<String> lines = new ArrayList<>();
        for (Pair<Instant, String> line : logHistory) {
            lines.add(formatLine(line.getFirst(), line.getSecond()));
        }
        return lines.toArray(new String[0]);
    }
}
