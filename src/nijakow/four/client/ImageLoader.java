package nijakow.four.client;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ImageLoader extends SwingWorker<ImageIcon, Object> {
    private final String argument;
    private final int position;
    private final JTextPane pane;

    public ImageLoader(String argument, int position, JTextPane pane) {
        this.argument = argument;
        this.position = position;
        this.pane = pane;
    }

    @Override
    protected ImageIcon doInBackground() {
        String splitter = argument;
        if (argument.startsWith(Commands.Codes.SPECIAL_IMG))
            splitter = argument.substring(Commands.Codes.SPECIAL_IMG.length());
        int cross = splitter.indexOf('x');
        int firstComma = splitter.indexOf(',');
        int width = -1, height = -1;
        String desc = null;
        if (cross >= 0 && firstComma > cross) {
            try {
                width = Integer.parseInt(splitter.substring(0, cross));
            } catch (NumberFormatException e) {
                width = -1;
            }
            try {
                height = Integer.parseInt(splitter.substring(cross + 1, firstComma));
            } catch (NumberFormatException e) {
                height = -1;
            }
            int secondComma = splitter.indexOf(',', firstComma + 1);
            if (secondComma >= 0) {
                desc = splitter.substring(firstComma + 1, secondComma);
                splitter = splitter.substring(secondComma + 1);
            } else {
                splitter = splitter.substring(firstComma + 1);
            }
        } else if (firstComma >= 0) {
            desc = splitter.substring(0, firstComma);
            splitter = splitter.substring(firstComma + 1);
        }
        ImageIcon i;
        try {
            i = new ImageIcon(new URL(splitter));
            if (desc != null) {
                i.setDescription(desc);
            }
            if (height == -1 && width >= 0) {
                height = i.getIconHeight() / (i.getIconWidth() / width);
            } else if (width == -1 && height >= 0) {
                width = i.getIconWidth() / (i.getIconHeight() / height);
            }
            if (width >= 0 || height >= 0) {
                i = new ImageIcon(i.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
            }
        } catch (MalformedURLException e) {
            i = null;
        }
        return i;
    }

    @Override
    protected void done() {
        ImageIcon i;
        try {
            i = get();
        } catch (InterruptedException | ExecutionException e) {
            i = null;
        }
        if (i != null) {
            pane.setCaretPosition(position);
            pane.insertIcon(i);
        }
    }
}
