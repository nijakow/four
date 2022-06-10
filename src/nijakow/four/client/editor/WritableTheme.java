package nijakow.four.client.editor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

import static nijakow.four.client.editor.WritableTheme.Types.*;
public class WritableTheme extends FTheme {
    private final List<TokenStyle> styles;

    public WritableTheme() {
        styles = new LinkedList<>();
    }

    private String styleToText(TokenStyle style) {
        final StringBuilder builder = new StringBuilder();
        if (style.getParent() != null && !styles.contains(style.getParent())) {
            builder.append(styleToText(style.getParent()))
                    .append("\n\n");
        }
        builder.append("type ");
        builder.append(style.getTokenType());
        if (style.getParent() != null) builder.append(" : ").append(style.getParent().getTokenType());
        builder.append(" {\n");
        if (style.isBoldOverwritten()) {
            builder.append("    ").
                    append(BOLD).
                    append(" = ").
                    append(style.isBold()).
                    append('\n');
        }
        if (style.isItalicOverwritten()) {
            builder.append("    ").
                    append(ITALIC).
                    append(" = ").
                    append(style.isItalic()).
                    append('\n');
        }
        if (style.isUnderlinedOverwritten()) {
            builder.append("    ").
                    append(UNDERLINE).
                    append(" = ").
                    append(style.isUnderlined()).
                    append('\n');
        }
        if (style.isStrikeThroughOverwritten()) {
            builder.append("    ").
                    append(STRIKE_THROUGH).
                    append(" = ").
                    append(style.isStrikeThrough()).
                    append('\n');
        }
        if (style.isAlignmentOverwritten()) {
            builder.append("    ").
                    append(ALIGNMENT).
                    append(" = ").
                    append(style.getAlignment()).
                    append('\n');
        }
        if (style.isBidiLevelOverwritten()) {
            builder.append("    ").
                    append(BIDI).
                    append(" = ").
                    append(style.getBidiLevel()).
                    append('\n');
        }
        if (style.isForegroundOverwritten()) {
            builder.append("    ").
                    append(FOREGROUND).
                    append(" = ").
                    append("0x").
                    append(Integer.toHexString(style.getForeground().getRGB()).substring(2)).
                    append('\n');
        }
        if (style.isBackgroundOverwritten()) {
            builder.append("    ").
                    append(BACKGROUND).
                    append(" = ").
                    append("0x").
                    append(Integer.toHexString(style.getBackground().getRGB()).substring(2)).
                    append('\n');
        }
        if (style.isSizeOverwritten()) {
            builder.append("    ").
                    append(SIZE).
                    append(" = ").
                    append(style.getSize()).
                    append('\n');
        }
        if (style.isFirstLineIndentOverwritten()) {
            builder.append("    ").
                    append(FL_INDENT).
                    append(" = ").
                    append(style.getFirstLineIndent()).
                    append('\n');
        }
        if (style.isFamilyOverwritten()) {
            builder.append("    ").
                    append(FONT).
                    append(" = ").
                    append(style.getFamily()).
                    append('\n');
        }
        builder.append("}");
        styles.add(style);
        return builder.toString();
    }

    private String generateText() {
        StringBuilder builder = new StringBuilder();
        styles.clear();
        for (TokenStyle style : getAllStyles()) {
            if (!styles.contains(style)) builder.append(styleToText(style)).append("\n\n");
        }
        return builder.toString();
    }

    public void saveToFile(File file) throws IOException {
        try (BufferedOutputStream os = new BufferedOutputStream(Files.newOutputStream(file.toPath()))) {
            os.write(generateText().getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
    }

    public abstract static class Types {
        public static final String ALIGNMENT      = "alignment";
        public static final String BACKGROUND     = "background";
        public static final String BIDI           = "bidilevel";
        public static final String BOLD           = "bold";
        public static final String FL_INDENT      = "firstline-indent";
        public static final String FONT           = "font";
        public static final String FOREGROUND     = "foreground";
        public static final String ITALIC         = "italic";
        public static final String SIZE           = "size";
        public static final String STRIKE_THROUGH = "strike-through";
        public static final String UNDERLINE      = "underlined";
    }
}
