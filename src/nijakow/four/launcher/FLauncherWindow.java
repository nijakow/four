package nijakow.four.launcher;

import nijakow.four.client.ClientWindow;
import nijakow.four.server.Four;
import nijakow.four.server.nvfs.NVFileSystem;
import nijakow.four.server.users.IdentityDatabase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class FLauncherWindow extends JFrame implements ActionListener {
    private static final String SERVER = "server";
    private static final String CLIENT = "client";
    private int[] ports;
    private boolean guestEnable;
    private String hostname;
    private String storagePath;
    private IdentityDatabase db;

    public FLauncherWindow(int[] ports, boolean guestEnable, String hostname, String storage, IdentityDatabase db) {
        super("Four: Launcher");
        this.ports = ports;
        this.guestEnable = guestEnable;
        this.hostname = hostname;
        this.storagePath = storage;
        this.db = db;
        JPanel content = new JPanel(new BorderLayout());
        JPanel launchButtons = new JPanel(new GridLayout(1, 2));
        JButton launchServer = new JButton("Launch Server...");
        launchServer.setActionCommand(SERVER);
        launchServer.addActionListener(this);
        JButton launchClient = new JButton("Launch Client...");
        launchClient.setActionCommand(CLIENT);
        launchClient.addActionListener(this);
        launchButtons.add(launchServer);
        launchButtons.add(launchClient);
        content.add(launchButtons, BorderLayout.SOUTH);
        getContentPane().add(content);
        pack();
        setLocationRelativeTo(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        // TODO if something is running, only close the window
        System.exit(0);
    }

    private void launchServer() {
        // TODO Better threading model!!!
        try {
            new Thread(new Four(db, new NVFileSystem(db), storagePath, hostname == null ? "localhost" : hostname, ports)).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not open the library!",
                    "Four: Server", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void launchClient() {
        ClientWindow.openWindow(hostname, ports);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case SERVER: launchServer(); break;
            case CLIENT: launchClient(); break;
        }
    }

    public static void showWindow(int[] ports, boolean guestEnable, String hostname, String storage, IdentityDatabase db) {
        EventQueue.invokeLater(() -> new FLauncherWindow(ports, guestEnable, hostname, storage, db).setVisible(true));
    }
}
