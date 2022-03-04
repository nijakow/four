package nijakow.four.share.lang;

import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.share.lang.c.parser.StreamPosition;
import nijakow.four.share.util.Pair;

public class FourCompilerException extends FourRuntimeException {
    private String errorText;

    public FourCompilerException(StreamPosition pos, String message) {
        super(message);
        this.errorText = makeErrorText(pos, message);
    }

    public String getErrorText() {
        return this.errorText;
    }

    private String makeErrorText(StreamPosition pos, String message) {
        StringBuilder sb = new StringBuilder();
        if (pos == null) {
            sb.append("?:?: ");
            sb.append(message);
        } else {
            String text = pos.getText();
            Pair<Integer, Integer> lp = pos.getPos();

            int l = 1;
            int c = 1;
            boolean newline = true;
            for (int index = 0; index < text.length(); index++) {
                if (l > lp.getFirst() - 5 && l < lp.getFirst() + 5) {
                    if (newline)
                        sb.append(String.format("%04d  ", l));
                    newline = false;
                    sb.append(text.charAt(index));
                }
                if (text.charAt(index) == '\n') {
                    newline = true;
                    if (l == lp.getFirst()) {
                        sb.append("      ");    // Line spacing
                        for (int xx = 1; xx < lp.getSecond(); xx++)
                            sb.append(' ');
                        sb.append('^');
                        sb.append(' ');
                        sb.append(lp.getFirst() + ":" + lp.getSecond() + ": ");
                        sb.append(message);
                        sb.append('\n');
                    }
                    l++;
                    c = 1;
                } else {
                    c++;
                }
            }
        }
        return sb.toString();
    }
}
