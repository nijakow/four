package nijakow.four.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import nijakow.four.client.net.ClientConnection;

public class ClientWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String SETTINGS = "settings";
	private static final String SEND = "send";
	private static final String STATUS_LABEL_TIMER = "invisible";
	private JLabel connectionStatus;
	private JTextField prompt;
	private JTextPane area;
	private StyledDocument term;
	private PreferencesHelper prefs;
	private ClientConnection connection;
	private Timer labelTimer;
	private boolean reconnect;
	private boolean bother;
	private ScheduledFuture<?> reconnectorHandler;
	private final ScheduledExecutorService queue;
	private final Runnable reconnector = () -> {
		connection = ClientConnection.getClientConnection(prefs.getHostname(), prefs.getPort());
		connection.setClientReceiveListener(message -> EventQueue.invokeLater(() -> {
			try {
				term.insertString(term.getLength(), message, null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}));
		try {
			connection.establishConnection();
			EventQueue.invokeLater(() -> {
				connectionStatus.setText(" Connected!");
				connectionStatus.setForeground(Color.green);
				labelTimer.start();
			});
			connection.openStreams();
		} catch (IOException ex) {
			EventQueue.invokeLater(() -> {
				if (bother)
					JOptionPane.showMessageDialog(this,
							"Could not connect to \"" + prefs.getHostname() + "\" on port " + prefs.getPort(),
							"Connection failed", JOptionPane.ERROR_MESSAGE);
				bother = false;
				connectionStatus.setVisible(true);
				connectionStatus.setText(" Not connected!");
				connectionStatus.setForeground(Color.red);
			});
		}
	};
	
	public ClientWindow(int[] ports) {
		super("Nijakow's \"Four\"");

		final Font font = new Font("Monospaced", Font.PLAIN, 14);
		
		// TODO macOS customization
		// TODO C editor
		// TODO iterate through ports
		bother = true;
		prefs = new PreferencesHelper();
		queue = Executors.newScheduledThreadPool(2);
		if (ports.length > 0)
			prefs.setPort(ports[0]);
		getContentPane().setLayout(new BorderLayout());
		JPanel south = new JPanel();
		south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
		prompt = new JTextField();
		prompt.setFont(font);
		prompt.addActionListener(this);
		prompt.setActionCommand(SEND);
		connectionStatus = new JLabel();
		getContentPane().add(connectionStatus, BorderLayout.NORTH);
		JButton settings = new JButton("Settings");
		settings.addActionListener(this);
		settings.setActionCommand(SETTINGS);
		south.add(prompt);
		south.add(settings);
		getContentPane().add(south, BorderLayout.SOUTH);
		area = new JTextPane();
		area.setEditable(false);
		area.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		area.setFont(font);
		term = area.getStyledDocument();
		addStyles();
		//area.setLineWrap(prefs.getLineBreaking());
		JScrollPane pane = new JScrollPane(area);
		getContentPane().add(pane, BorderLayout.CENTER);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
		labelTimer = new Timer(5000, this);
		labelTimer.setActionCommand(STATUS_LABEL_TIMER);
		labelTimer.setRepeats(false);
		reconnectorHandler = queue.scheduleAtFixedRate(reconnector, 0, 5, TimeUnit.SECONDS);	// Must be last
	}
	
	public void addStyles() {
		// TODO
	}
	
	public void dispose() {
		prefs.setWindowDimensions(getX(), getY(), getWidth(), getHeight());
		prefs.flush();
		closeConnection();
		super.dispose();
	}
	
	private void closeConnection() {
		reconnectorHandler.cancel(true);
		if (connection != null)
			connection.close();
		bother = true;
	}
	
	private void openSettingsWindow() {
		JDialog settingsWindow = new JDialog(this, "Four: Settings", true);
		settingsWindow.getContentPane().setLayout(new GridLayout(3, 1));
		JPanel hostPa = new JPanel();
		hostPa.setLayout(new GridLayout(2, 1));
		hostPa.add(new JLabel("The hostname to connect to:"));
		JTextField hostname = new JTextField();
		hostPa.add(hostname);
		hostPa.setBorder(new EtchedBorder());
		settingsWindow.getContentPane().add(hostPa);
		JPanel portPa = new JPanel();
		portPa.setLayout(new GridLayout(2, 1));
		portPa.add(new JLabel("The port to use: "));
		JTextField portNo = new JTextField();
		portPa.add(portNo);
		portPa.setBorder(new EtchedBorder());
		settingsWindow.getContentPane().add(portPa);
		JCheckBox lineBreak = new JCheckBox("Automated line breaking");
		settingsWindow.getContentPane().add(lineBreak);
		settingsWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				hostname.setText(prefs.getHostname());
				portNo.setText(Integer.toString(prefs.getPort()));
				lineBreak.setSelected(prefs.getLineBreaking());
			}
			
			private void storeSettings() {
				String host = hostname.getText();
				if (!prefs.getHostname().equals(host)) {
					prefs.setHostname(hostname.getText());
					reconnect = true;
				}
				prefs.setLineBreaking(lineBreak.isSelected());
				try {
					int port = Integer.parseInt(portNo.getText());
					if (port != prefs.getPort()) {
						prefs.setPort(port);
						reconnect = true;
					}
				} catch (NumberFormatException ex) {
					System.err.println("Not a valid port: \"" + portNo.getText() +"\", ignored.");
				}
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				storeSettings();
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				storeSettings();
				prefs.flush();
				if (reconnect) {
					closeConnection();
					reconnectorHandler = queue.scheduleAtFixedRate(reconnector, 0, 5, TimeUnit.SECONDS);
					reconnect = false;
				}
				//area.setLineWrap(prefs.getLineBreaking());
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
			
		case STATUS_LABEL_TIMER:
			connectionStatus.setVisible(false);
			break;
			
		case SEND:
			String text = prompt.getText() + "\n";
			try {
				term.insertString(term.getLength(), text, null);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
			prompt.setText("");
			queue.schedule(() -> {
				try {
					connection.send(text);
				} catch (Exception ex) {
					EventQueue.invokeLater(() -> {
						try {
							term.insertString(term.getLength(), "*** Could not send message --- see console for more details! ***\n", null);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
					});
				}
			}, 0, TimeUnit.NANOSECONDS);
			break;
		}
	}
	
	public static void openWindow(int[] ports) {
		EventQueue.invokeLater(() -> new ClientWindow(ports).setVisible(true));
	}
}