package nijakow.four.launcher;

import nijakow.four.client.ClientWindow;
import nijakow.four.server.Four;
import nijakow.four.server.nvfs.NVFileSystem;
import nijakow.four.server.runtime.exceptions.FourRuntimeException;
import nijakow.four.server.users.IdentityDatabase;
import nijakow.four.share.lang.base.CompilationException;
import nijakow.four.share.lang.c.parser.ParseException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class FLauncherWindow extends JFrame implements ActionListener {
    private static final String SERVER   = "server";
    private static final String CLIENT   = "client";
    private static final String CHOOSE   = "choose";
    private static final String ROOT_PWD = "root-pwd";
    private int[] ports;
    private File libDir;
    private boolean guestEnable;
    private String hostname;
    private String storagePath;
    private final IdentityDatabase db;
    private final NVFileSystem fs;
    private final JLabel pathEnter;

    public FLauncherWindow(int[] ports, boolean guestEnable, String hostname, String storage, IdentityDatabase db, NVFileSystem fs) {
        super("Four: Launcher");
        this.ports = ports;
        this.guestEnable = guestEnable;
        this.hostname = hostname;
        this.storagePath = storage;
        this.db = db;
        this.fs = fs;
        libDir = null;
        JPanel content = new JPanel(new BorderLayout());
        JPanel options = new JPanel(new GridLayout(2, 1));
        JPanel path = new JPanel(new GridLayout(2, 1));
        JPanel entering = new JPanel(new BorderLayout());
        JLabel pathDesc = new JLabel("Enter the path to the four library:");
        JButton pathSelect = new JButton("Load...");
        pathSelect.setActionCommand(CHOOSE);
        pathSelect.addActionListener(this);
        pathEnter = new JLabel();
        pathEnter.setFont(pathEnter.getFont().deriveFont(Font.BOLD));
        entering.add(pathEnter, BorderLayout.CENTER);
        entering.add(pathSelect, BorderLayout.EAST);
        path.add(pathDesc);
        path.add(entering);
        path.setBorder(new EtchedBorder());
        options.add(path);
        JButton rootPwd = new JButton("Set root's password");
        rootPwd.setActionCommand(ROOT_PWD);
        rootPwd.addActionListener(this);
        content.add(rootPwd, BorderLayout.NORTH);
        content.add(options, BorderLayout.CENTER);
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
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void launchServer() {
        // TODO Better threading model!!!
        new Thread(() -> {
            try (Four f = new Four(db, fs, storagePath, hostname == null ? "localhost" : hostname, ports)) {
                f.loop();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Could not open the library!",
                        "Four: Server", JOptionPane.ERROR_MESSAGE);
            } catch (FourRuntimeException e) {
                JOptionPane.showMessageDialog(this, "Could not open master.c!",
                        "Four: Server", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Could not kill server!",
                        "Four: Server", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private void launchClient() {
        ClientWindow.openWindow(hostname, ports);
    }

    private void chooseLib() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDragEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            try {
                fs.load(dir, db);
                libDir = dir;
                pathEnter.setText(libDir.getAbsolutePath());
            } catch (CompilationException | ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Could not open library!",
                        "Four: Launcher", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setRootPassword() {
        JPanel content = new JPanel(new GridLayout(2, 1));
        content.add(new JLabel("Enter the password for user \"root\":"));
        JPasswordField pwd = new JPasswordField();
        content.add(pwd);
        if (JOptionPane.showOptionDialog(this, content, "Four: Launcher",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null) == JOptionPane.OK_OPTION) {
            db.getRootUser().setPassword(String.valueOf(pwd.getPassword()));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case SERVER: launchServer(); break;
            case CLIENT: launchClient(); break;
            case CHOOSE: chooseLib(); break;
            case ROOT_PWD: setRootPassword(); break;
        }
    }

    public static void showWindow(int[] ports, boolean guestEnable, String hostname, String storage, IdentityDatabase db, NVFileSystem fs) {
        EventQueue.invokeLater(() -> new FLauncherWindow(ports, guestEnable, hostname, storage, db, fs).setVisible(true));
    }
}
