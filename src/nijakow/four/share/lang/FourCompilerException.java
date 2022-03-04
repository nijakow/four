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
            Pair<Integer, Integer> lp = pos.getPos();
            sb.append(lp.getFirst() + ":" + lp.getSecond() + ": ");
            sb.append(message);
            sb.append('\n');
            String text = pos.getText();
            int min = -30;
            int max = 30;
            for (int x = min; x <= max; x++) {
                if (x == 0) sb.append(" --> ");
                char c;

                try {
                    c = text.charAt(pos.getIndex() + x);
                } catch (StringIndexOutOfBoundsException e) {
                    c = ' ';
                }
                if (c < 32 || c >= 127)
                    c = ' ';
                sb.append(c);
                if (x == 0) sb.append(" <-- ");
            }
        }
        return sb.toString();
    }
}
