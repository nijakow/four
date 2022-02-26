package nijakow.four.client.utils;

import javax.swing.*;

public abstract class UIHelper {
    public static UIManager.LookAndFeelInfo getLookAndFeelByName(String name) {
        for (UIManager.LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) {
            if (i.getName().equals(name)) return i;
        }
        return null;
    }

    public static boolean setLookAndFeelByName(String name)
            throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        UIManager.LookAndFeelInfo i = getLookAndFeelByName(name);
        if (i == null) {
            return false;
        }
        UIManager.setLookAndFeel(i.getClassName());
        return true;
    }
}
