package nijakow.four.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
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
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import nijakow.four.client.editor.ClientEditor;
import nijakow.four.client.net.ClientConnection;
import nijakow.four.client.net.ClientConnectionListener;
import nijakow.four.client.utils.StringHelper;

public class ClientWindow extends JFrame implements ActionListener, ClientConnectionListener {
	private static final long serialVersionUID = 1L;
	private String buffer;
	private JLabel connectionStatus;
	private JLabel promptText;
	private JScrollPane pane;
	private JTextField prompt;
	private JPasswordField pwf;
	private JTextPane area;
	private StyledDocument term;
	private PreferencesHelper prefs;
	private ClientConnection connection;
	private Timer labelTimer;
	private boolean reconnect;
	private boolean bother;
	private boolean wasSpecial;
	private Style current;
	private ScheduledFuture<?> reconnectorHandler;
	private final ScheduledExecutorService queue;
	private final Runnable reconnector = () -> {
		connection = ClientConnection.getClientConnection(prefs.getHostname(), prefs.getPort());
		connection.setClientConnectionListener(this);
		try {
			connection.establishConnection();
			EventQueue.invokeLater(() -> {
				connectionStatus.setText(" Connected!");
				connectionStatus.setForeground(Color.green);
				labelTimer.start();
			});
			reconnectorHandler.cancel(false);
			connection.openStreams();
		} catch (IOException ex) {
			if (!reconnectorHandler.isCancelled()) {
				EventQueue.invokeLater(() -> {
					if (bother) {
						bother = false;
						JOptionPane.showMessageDialog(this,
								"Could not connect to \"" + prefs.getHostname() + "\" on port " + prefs.getPort(),
								"Connection failed", JOptionPane.ERROR_MESSAGE);
					}
					labelTimer.stop();
					connectionStatus.setVisible(true);
					connectionStatus.setText(" Not connected!");
					connectionStatus.setForeground(Color.red);
				});
			}
		}
	};
	
	public ClientWindow(int[] ports) {
		super("Nijakow's \"Four\"");

		final Font font = new Font("Monospaced", Font.PLAIN, 14);
		
		// TODO macOS customization
		// TODO C editor
		// TODO iterate through ports
		buffer = "";
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
		prompt.setActionCommand(Commands.ACTION_SEND);
		pwf = new JPasswordField();
		pwf.setFont(font);
		pwf.addActionListener(this);
		pwf.setActionCommand(Commands.ACTION_PASSWORD);
		pwf.setEchoChar('*');
		promptText = new JLabel();
		connectionStatus = new JLabel();
		getContentPane().add(connectionStatus, BorderLayout.NORTH);
		JButton settings = new JButton("Settings");
		settings.addActionListener(this);
		settings.setActionCommand(Commands.ACTION_SETTINGS);
		south.add(promptText);
		south.add(prompt);
		south.add(pwf);
		pwf.setVisible(false);
		south.add(settings);
		getContentPane().add(south, BorderLayout.SOUTH);
		area = new JTextPane();
		area.setEditable(false);
		area.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		area.setFont(font);
		area.setOpaque(true);
		term = area.getStyledDocument();
		addStyles();
		pane = new JScrollPane();
		setLineBreaking(prefs.getLineBreaking());
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
		setMinimumSize(new Dimension(300, 200));
		setPreferredSize(new Dimension(750, 500));
		if (width == -1 || height == -1)
			pack();
		else
			setSize(width, height);
		labelTimer = new Timer(5000, this);
		labelTimer.setActionCommand(Commands.ACTION_STATUS_LABEL_TIMER);
		labelTimer.setRepeats(false);
		reconnectorHandler = queue.scheduleWithFixedDelay(reconnector, 0, 5, TimeUnit.SECONDS);
	}
	
	private void setLineBreaking(boolean breaking) {
		if (breaking)
			pane.setViewportView(area);else {
			JPanel wrap = new JPanel();
			wrap.setLayout(new BorderLayout());
			wrap.add(area);
			pane.setViewportView(wrap);
		}
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (prompt.isVisible())
			prompt.requestFocusInWindow();
		else
			pwf.requestFocusInWindow();
	}
	
	private void addStyles() {
		final Style def = area.getLogicalStyle();
		Style s = term.addStyle(Commands.STYLE_ERROR, def);
		StyleConstants.setBold(s, true);
		StyleConstants.setItalic(s, true);
		StyleConstants.setForeground(s, Color.red);
		s = term.addStyle(Commands.STYLE_INPUT, def);
		StyleConstants.setForeground(s, Color.gray);
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
				setLineBreaking(prefs.getLineBreaking());
			}
		});
		settingsWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		settingsWindow.setLocationRelativeTo(this);
		settingsWindow.setResizable(false);
		settingsWindow.pack();
		settingsWindow.setVisible(true);
	}
	
	private Style getStyleByName(String style) {
		Style ret = term.addStyle(style, current == null ? area.getLogicalStyle() : current);
		switch (style) {
		case Commands.STYLE_NORMAL:
			ret = null;
			break;
			
		case Commands.STYLE_BLUE:
			StyleConstants.setForeground(ret, Color.blue);
			break;
			
		case Commands.STYLE_RED:
			StyleConstants.setForeground(ret, Color.red);
			break;

		case Commands.STYLE_GREEN:
			StyleConstants.setForeground(ret, Color.green);
			break;

		case Commands.STYLE_YELLOW:
			StyleConstants.setForeground(ret, Color.yellow);
			break;
			
		case Commands.STYLE_BLACK:
			StyleConstants.setForeground(ret, Color.black);
			break;

		case Commands.STYLE_ITALIC:
			StyleConstants.setItalic(ret, true);
			break;
			
		case Commands.STYLE_BOLD:
			StyleConstants.setBold(ret, true);
			break;
			
		case Commands.STYLE_UNDERSCORED:
			StyleConstants.setUnderline(ret, true);
			break;
			
		case Commands.STYLE_BG_BLACK:
			StyleConstants.setBackground(ret, Color.black);
			break;
			
		case Commands.STYLE_BG_BLUE:
			StyleConstants.setBackground(ret, Color.blue);
			break;
			
		case Commands.STYLE_BG_GREEN:
			StyleConstants.setBackground(ret, Color.green);
			break;
			
		case Commands.STYLE_BG_RED:
			StyleConstants.setBackground(ret, Color.red);
			break;
			
		case Commands.STYLE_BG_YELLOW:
			StyleConstants.setBackground(ret, Color.yellow);
			break;
		}
		return ret;
	}
	
	private void parseArgument(String arg) {
		if (arg.startsWith(Commands.SPECIAL_PWD)) {
			EventQueue.invokeLater(() -> {
				pwf.setVisible(true);
				prompt.setVisible(false);
				pwf.requestFocusInWindow();
				if (arg.length() > Commands.SPECIAL_PROMPT.length() + 1)
					promptText.setText(arg.substring(Commands.SPECIAL_PWD.length(), arg.length()));
				else
					promptText.setText("");
				validate();
			});
		} else if (arg.startsWith(Commands.SPECIAL_PROMPT)) {
			EventQueue.invokeLater(() -> {
				if (arg.length() > Commands.SPECIAL_PROMPT.length() + 1)
					promptText.setText(arg.substring(Commands.SPECIAL_PWD.length(), arg.length()));
				else
					promptText.setText("");
			});
		} else if (arg.startsWith(Commands.SPECIAL_EDIT)) {
			String splitter = arg.substring(Commands.SPECIAL_EDIT.length(), arg.length());
			int i0 = splitter.indexOf(Commands.SPECIAL_RAW);
			if (i0 < 0) return;
			String id = splitter.substring(0, i0);
			splitter = splitter.substring(i0 + 1);
			int i1 = splitter.indexOf(Commands.SPECIAL_RAW);
			if (i1 < 0) return;
			String title = splitter.substring(0, i1);
			splitter = splitter.substring(i1 + 1);
			openEditor(new String[] { id, title, splitter });
		} else
			current = getStyleByName(arg);
	}
	
	private void openEditor(String[] args) {
		new ClientEditor(this, connection, queue, args).setVisible(true);
	}
	
	@Override
	public void charReceived(ClientConnection connection, char c) {
		EventQueue.invokeLater(() -> {
			try {
				if (c == Commands.SPECIAL_END) {
					wasSpecial = false;
					parseArgument(buffer);
				} else if (wasSpecial)
					buffer += c;
				else if (c == Commands.SPECIAL_START) {
					wasSpecial = true;
					buffer = "";
				}
				else
					term.insertString(term.getLength(), Character.toString(c), current);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		});
	}
	
	@Override
	public void connectionLost(ClientConnection connection) {
		EventQueue.invokeLater(() -> {
			prompt.setText(" Connection closed. ");
			prompt.setEnabled(false);
			pwf.setText(" Connection lost." );
			pwf.setEchoChar((char) 0);
			pwf.setEnabled(false);
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JTextField tmp = prompt;
		switch (e.getActionCommand()) {
		case Commands.ACTION_SETTINGS:
			openSettingsWindow();
			break;
			
		case Commands.ACTION_STATUS_LABEL_TIMER:
			connectionStatus.setVisible(false);
			break;
			
		case Commands.ACTION_PASSWORD:
			prompt.setVisible(true);
			pwf.setVisible(false);
			prompt.requestFocusInWindow();
			validate();
			tmp = pwf;
			
		case Commands.ACTION_SEND:
			// TODO Don't reset background
			String text = tmp.getText() + "\n";
			tmp.enableInputMethods(true);
			try {
				term.insertString(term.getLength(), promptText.getText() + " ", null);
				final Style s = term.getStyle(Commands.STYLE_INPUT);
				if (tmp == prompt)
					term.insertString(term.getLength(), text, s);
				else
					term.insertString(term.getLength(), StringHelper.generateFilledString('*', text.length() - 1) + "\n", s);
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
			tmp.setText("");
			queue.schedule(() -> {
				try {
					connection.send(text);
				} catch (Exception ex) {
					EventQueue.invokeLater(() -> {
						try {
							term.insertString(term.getLength(), "*** Could not send message --- see console for more details! ***\n", term.getStyle(Commands.STYLE_ERROR));
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
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Could not set UI - will use default UI.");
		}
		EventQueue.invokeLater(() -> new ClientWindow(ports).setVisible(true));
	}
}