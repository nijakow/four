package nijakow.four.smalltalk.logging;

import nijakow.four.smalltalk.util.Pair;

import java.io.PrintWriter;
import java.io.StringWriter;
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
        String padded = i.toString();
        while (padded.length() < 30)
            padded += " ";
        return String.format("%s %s", "[" + padded + "]:", l);
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

    public String[] getLines(int amount) {

        List<String> lines = new ArrayList<>();
        for (int start = (amount < 0) ? 0 : Math.max(0, logHistory.size() - amount); start < logHistory.size(); start++) {
            final Pair<Instant, String> line = logHistory.get(start);
            lines.add(formatLine(line.getFirst(), line.getSecond()));
        }
        return lines.toArray(new String[0]);
    }
}
