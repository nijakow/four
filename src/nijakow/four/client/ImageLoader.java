package nijakow.four.client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ImageLoader extends SwingWorker<ImageIcon, Object> {
    private final String argument;
    private final int position;
    private final int visibleHeight;
    private final int visibleWidth;
    private final JTextPane pane;

    public ImageLoader(String argument, int position, JTextPane pane, int height, int width) {
        this.argument = argument;
        this.position = position;
        this.pane = pane;
        this.visibleHeight = height;
        this.visibleWidth = width;
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
            String w = splitter.substring(0, cross);
            String h = splitter.substring(cross + 1, firstComma);
            if (w.equals("?")) {
                width = visibleWidth;
            } else {
                try {
                    width = Integer.parseInt(w);
                } catch (NumberFormatException e) {
                    width = -1;
                }
            }
            if (h.equals("?")) {
                height = visibleHeight;
            } else {
                try {
                    height = Integer.parseInt(h);
                } catch (NumberFormatException e) {
                    height = -1;
                }
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
            BufferedImage ii = ImageIO.read(new URL(splitter));
            i = new ImageIcon(ii);
            if (desc != null) {
                i.setDescription(desc);
            }
            if (height == -1 && width >= 0) {
                float scale = (float) i.getIconWidth() / width;
                height = (int) (i.getIconHeight() / scale);
            } else if (width == -1 && height >= 0) {
                float scale = (float) i.getIconHeight() / height;
                width = (int) (i.getIconWidth() / scale);
            }
            if (width >= 0 || height >= 0) {
                i = new ImageIcon(i.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
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
            pane.setSelectionStart(position - 1);
            pane.setSelectionEnd(position);
            pane.insertIcon(i);
        }
    }
}
