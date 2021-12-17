package nijakow.four.client;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import nijakow.four.client.net.ClientConnection;
import nijakow.four.client.net.ClientConnectionImpl;

public class ClientWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String SETTINGS = "settings";
	private static final String SEND = "send";
	private JLabel connectionStatus;
	private JTextField prompt;
	private JTextArea area;
	private PreferencesHelper prefs;
	private ClientConnection connection;
	
	public ClientWindow() {
		super("Nijakow's \"Four\"");
		// TODO macOS customization
		prefs = new PreferencesHelper();
		new Thread(() -> connection = new ClientConnectionImpl(prefs, true)).start();
		getContentPane().setLayout(new BorderLayout());
		JPanel south = new JPanel();
		south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
		prompt = new JTextField();
		prompt.addActionListener(this);
		prompt.setActionCommand(SEND);
		connectionStatus = new JLabel();
		getContentPane().add(connectionStatus, BorderLayout.NORTH);
		connectionStatus.setVisible(false);
		JButton settings = new JButton("Settings");
		settings.addActionListener(this);
		settings.setActionCommand(SETTINGS);
		south.add(prompt);
		south.add(settings);
		getContentPane().add(south, BorderLayout.SOUTH);
		area = new JTextArea();
		area.setEditable(false);
		area.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		JScrollPane pane = new JScrollPane(area);
		getContentPane().add(pane, BorderLayout.CENTER);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				prefs.setWindowDimensions(getX(), getY(), getWidth(), getHeight());
				prefs.flush();
				connection.close();
				dispose();
			}
		});
		int x = prefs.getWindowPositionX();
		int y = prefs.getWindowPositionY();
		if (x == -1 || y == -1)
			setLocationRelativeTo(null);
		else
			setLocation(x, y);
		int width = prefs.getWindowWidth();
		int height = prefs.getWindowHeight();
		if (width == -1 || height == -1)
			pack();
		else
			setSize(width, height);
	}
	
	private void openSettingsWindow() {
		JDialog settingsWindow = new JDialog(this, "Four: Settings", true);
		settingsWindow.getContentPane().setLayout(new GridLayout(3, 1));
		JPanel host = new JPanel();
		host.setLayout(new GridLayout(2, 1));
		host.add(new JLabel("The hostname to connect to:"));
		JTextField hostname = new JTextField();
		host.add(hostname);
		host.setBorder(new EtchedBorder());
		settingsWindow.getContentPane().add(host);
		JPanel port = new JPanel();
		port.setLayout(new GridLayout(2, 1));
		port.add(new JLabel("The port to use: "));
		JTextField portNo = new JTextField();
		port.add(portNo);
		port.setBorder(new EtchedBorder());
		settingsWindow.getContentPane().add(port);
		JCheckBox lineBreak = new JCheckBox("Automated line breaking");
		settingsWindow.getContentPane().add(lineBreak);
		settingsWindow.addWindowListener(new WindowAdapter() {
			boolean reconnect;
			
			@Override
			public void windowActivated(WindowEvent e) {
				hostname.setText(prefs.getHostname());
				portNo.setText(Integer.toString(prefs.getPort()));
				lineBreak.setSelected(prefs.getLineBreaking());
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				if (prefs.getHostname() != hostname.getText()) {
					prefs.setHostname(hostname.getText());
					reconnect = true;
				}
				prefs.setLineBreaking(lineBreak.isSelected());
				try {
					int p = Integer.parseInt(portNo.getText());
					if (p != prefs.getPort()) {
						prefs.setPort(p);
						reconnect = true;
					}
				} catch (NumberFormatException ex) {
					System.err.println("Not a valid port: \"" + portNo.getText() +"\", ignored.");
				}
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				prefs.flush();
				if (reconnect) {
					new Thread(() -> {
						connection.close();
						connection = new ClientConnectionImpl(prefs, false);
						try {
							((ClientConnectionImpl) connection).establishConnection();
						} catch (UnknownHostException ex) {
							EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(
									settingsWindow,
									"Could not connect to \"" + prefs.getHostname() + "\" on port " + prefs.getPort(),
									"Connection failed", JOptionPane.ERROR_MESSAGE));
						}
					}).start();
				}
			}
		});
		settingsWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		settingsWindow.setLocationRelativeTo(this);
		settingsWindow.setResizable(false);
		settingsWindow.pack();
		settingsWindow.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case SETTINGS:
			openSettingsWindow();
			break;
			
		case SEND:
			connection.send(prompt.getText());
			area.append(" > " + prompt.getText() + "\n");
			prompt.setText("");
			break;
		}
	}
	
	public static void openWindow() {
		EventQueue.invokeLater(() -> new ClientWindow().setVisible(true));
	}
}