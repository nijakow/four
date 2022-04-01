package nijakow.four.client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import nijakow.four.client.editor.ClientEditor;
import nijakow.four.client.net.ClientConnection;
import nijakow.four.client.net.ClientConnectionListener;
import nijakow.four.client.utils.StringHelper;
import nijakow.four.client.utils.UIHelper;

public class ClientWindow extends JFrame implements ActionListener, ClientConnectionListener {
	private static final long serialVersionUID = 1L;
	private final JLabel promptText;
	private final JButton reconnectButton;
	private final JScrollPane pane;
	private final JTextField prompt;
	private final JPasswordField pwf;
	private final JTextPane area;
	private final StyledDocument term;
	private final List<ClientEditor> editors;
	private int[] ports;
	private int portCounter;
	private String buffer;
	private JLabel connectionStatus;
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
		connection = ClientConnection.getClientConnection(prefs.getHostname(), ports[portCounter]);
		connection.setClientConnectionListener(this);
		boolean wasConnected = false;
		try {
			connection.establishConnection();
			wasConnected = true;
			EventQueue.invokeLater(() -> {
				labelTimer.stop();
				connectionStatus.setVisible(true);
				connectionStatus.setText("Connected!");
				connectionStatus.setForeground(Color.green);
				labelTimer.start();
			});
			reconnectorHandler.cancel(false);
			connection.openStreams();
		} catch (IOException ex) {
			if (!wasConnected) {
				int wasPort = ports[portCounter];
				EventQueue.invokeLater(() -> {
					if (bother) {
						bother = false;
						JOptionPane.showMessageDialog(this,
								"Could not connect to \"" + prefs.getHostname() + "\" on port " + wasPort,
								"Connection failed", JOptionPane.ERROR_MESSAGE);
					}
					labelTimer.stop();
					connectionStatus.setVisible(true);
					connectionStatus.setText("Not connected!");
					connectionStatus.setForeground(Color.red);
				});
				portCounter = portCounter == ports.length - 1 ? 0 : portCounter + 1;
				if (wasPort != ports[portCounter]) {
					bother = true;
				}
			}
		}
	};
	
	public ClientWindow(String hostname, int[] ports) {
		super("Nijakow's \"Four\"");

		final Font font = new Font("Monospaced", Font.PLAIN, 14);

		// TODO macOS customization
		buffer = "";
		bother = true;
		prefs = PreferencesHelper.getInstance();
		if (hostname != null)
			prefs.setHostname(hostname);
		try {
			if (!UIHelper.setLookAndFeelByName(prefs.getUIManagerName())) {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
		} catch (Exception e) {
			System.err.println("Could not set UI - will use default UI.");
		}
		queue = Executors.newScheduledThreadPool(2);
		this.ports = ports;
		if (ports.length == 0)
			prefs.setPort(ports[0]);
		portCounter = 0;
		getContentPane().setLayout(new BorderLayout());
		JPanel south = new JPanel();
		south.setOpaque(false);
		south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
		prompt = new JTextField();
		prompt.setFont(font);
		prompt.addActionListener(this);
		prompt.setActionCommand(Commands.Actions.ACTION_SEND);
		pwf = new JPasswordField();
		pwf.setFont(font);
		pwf.addActionListener(this);
		pwf.setActionCommand(Commands.Actions.ACTION_PASSWORD);
		promptText = new JLabel();
		promptText.setFont(font);
		reconnectButton = new JButton("Reconnect");
		reconnectButton.setActionCommand(Commands.Actions.ACTION_RECONNECT);
		reconnectButton.addActionListener(this);
		connectionStatus = new JLabel("", SwingConstants.CENTER);
		getContentPane().add(connectionStatus, BorderLayout.NORTH);
		JButton settings = new JButton("Settings");
		settings.addActionListener(this);
		settings.setActionCommand(Commands.Actions.ACTION_SETTINGS);
		south.add(promptText);
		south.add(prompt);
		south.add(pwf);
		south.add(reconnectButton);
		reconnectButton.setVisible(false);
		pwf.setVisible(false);
		south.add(settings);
		getContentPane().add(south, BorderLayout.SOUTH);
		area = new JTextPane();
		area.setEditable(false);
		area.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		area.setFont(font);
		term = area.getStyledDocument();
		term.addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				area.setCaretPosition(term.getLength());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		addStyles();
		pane = new JScrollPane();
		pane.setOpaque(false);
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
		editors = new LinkedList<>();
		toggleMode(prefs.getDarkMode());
		setMinimumSize(new Dimension(300, 200));
		setPreferredSize(new Dimension(750, 500));
		if (width == -1 || height == -1)
			pack();
		else
			setSize(width, height);
		labelTimer = new Timer(5000, this);
		labelTimer.setActionCommand(Commands.Actions.ACTION_STATUS_LABEL_TIMER);
		labelTimer.setRepeats(false);
		reconnectorHandler = queue.scheduleWithFixedDelay(reconnector, 0, 2500, TimeUnit.MILLISECONDS);
	}

	private void toggleMode(boolean dark) {
		if (dark) {
			pwf.setForeground(Color.white);
			pwf.setBackground(Color.gray);
			pwf.setCaretColor(Color.white);
			area.setForeground(Color.lightGray);
			area.setBackground(Color.black);
			prompt.setForeground(Color.white);
			prompt.setBackground(Color.gray);
			prompt.setCaretColor(Color.white);
			promptText.setForeground(Color.white);
			promptText.setBackground(Color.darkGray);
			connectionStatus.setBackground(Color.darkGray);
			getContentPane().setBackground(Color.darkGray);
		} else {
			pwf.setForeground(null);
			pwf.setBackground(Color.white);
			pwf.setCaretColor(null);
			area.setForeground(null);
			area.setBackground(Color.white);
			prompt.setForeground(null);
			prompt.setBackground(Color.white);
			prompt.setCaretColor(null);
			promptText.setForeground(null);
			promptText.setBackground(null);
			connectionStatus.setBackground(null);
			getContentPane().setBackground(null);
		}
		for (ClientEditor clientEditor : editors) {
			clientEditor.toggleMode(dark);
		}
	}

	private void setLineBreaking(boolean breaking) {
		if (breaking)
			pane.setViewportView(area);
		else {
			JPanel wrap = new JPanel();
			wrap.setOpaque(false);
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
		Style s = term.addStyle(Commands.Styles.STYLE_ERROR, def);
		StyleConstants.setBold(s, true);
		StyleConstants.setItalic(s, true);
		StyleConstants.setForeground(s, Color.red);
		s = term.addStyle(Commands.Styles.STYLE_INPUT, def);
		StyleConstants.setForeground(s, Color.gray);
	}

	private void disposeEditors() {
		for (ClientEditor ed : editors) {
			ed.dispose();
		}
	}

	@Override
	public void dispose() {
		prefs.setWindowDimensions(getX(), getY(), getWidth(), getHeight());
		prefs.flush();
		disposeEditors();
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
		settingsWindow.getContentPane().setLayout(new BoxLayout(settingsWindow.getContentPane(), BoxLayout.Y_AXIS));
		JPanel uis = new JPanel();
		uis.setLayout(new GridLayout(2, 1));
		uis.setOpaque(false);
		uis.setBorder(new EtchedBorder());
		JLabel uiDesc = new JLabel("The Look & Feel to use:");
		JComboBox<String> uiManagers = new JComboBox<>();
		uiManagers.setEditable(false);
		for (UIManager.LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) {
			uiManagers.addItem(i.getName());
			if (i.getName().equals(UIManager.getLookAndFeel().getName())) {
				uiManagers.setSelectedItem(i.getName());
			}
		}
		uiManagers.addItemListener(e -> {
			String selected = (String) uiManagers.getSelectedItem();
			if (!UIManager.getLookAndFeel().getName().equals(selected)) {
				try {
					UIHelper.setLookAndFeelByName(selected);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				/*
				 * Warning: updating the UI may cause internal NullPointerException by the combo-box!
				 * - mhahnFr
				 */
				SwingUtilities.updateComponentTreeUI(settingsWindow);
				SwingUtilities.updateComponentTreeUI(this);
				for (ClientEditor editor : editors) {
					SwingUtilities.updateComponentTreeUI(editor);
				}
				prefs.setUIManagerName(selected);
				settingsWindow.pack();
			}
		});
		uis.add(uiDesc);
		uis.add(uiManagers);
		settingsWindow.getContentPane().add(uis);
		JPanel hostPa = new JPanel();
		hostPa.setOpaque(false);
		hostPa.setLayout(new GridLayout(2, 1));
		JLabel hostPaLabel = new JLabel("The hostname to connect to:");
		hostPa.add(hostPaLabel);
		JTextField hostname = new JTextField();
		hostPa.add(hostname);
		hostPa.setBorder(new EtchedBorder());
		settingsWindow.getContentPane().add(hostPa);
		JPanel portPa = new JPanel();
		portPa.setOpaque(false);
		portPa.setLayout(new GridLayout(2, 1));
		JLabel portPaLabel = new JLabel("The port to use: ");
		portPa.add(portPaLabel);
		JTextField portNo = new JTextField();
		portPa.add(portNo);
		portPa.setBorder(new EtchedBorder());
		settingsWindow.getContentPane().add(portPa);
		JCheckBox lineBreak = new JCheckBox("Automated line breaking");
		lineBreak.addItemListener(event -> {
			prefs.setLineBreaking(lineBreak.isSelected());
			setLineBreaking(prefs.getLineBreaking());
		});
		JCheckBox darkMode = new JCheckBox("Dark mode");
		settingsWindow.getContentPane().add(darkMode);
		settingsWindow.getContentPane().add(lineBreak);
		darkMode.addItemListener(event -> {
			boolean dark = darkMode.isSelected();
			toggleMode(dark);
			prefs.setDarkMode(dark);
			if (dark) {
				settingsWindow.getContentPane().setBackground(Color.darkGray);
				darkMode.setForeground(Color.white);
				darkMode.setBackground(Color.darkGray);
				lineBreak.setForeground(Color.white);
				lineBreak.setBackground(Color.darkGray);
				hostname.setBackground(Color.gray);
				hostname.setCaretColor(Color.white);
				hostname.setForeground(Color.white);
				portNo.setBackground(Color.gray);
				portNo.setCaretColor(Color.white);
				portNo.setForeground(Color.white);
				hostPaLabel.setForeground(Color.white);
				portPaLabel.setForeground(Color.white);
				uiDesc.setForeground(Color.white);
				uiDesc.setBackground(Color.darkGray);
			} else {
				settingsWindow.getContentPane().setBackground(null);
				darkMode.setForeground(null);
				darkMode.setBackground(null);
				lineBreak.setForeground(null);
				lineBreak.setBackground(null);
				hostname.setBackground(Color.white);
				hostname.setCaretColor(null);
				hostname.setForeground(null);
				portNo.setBackground(Color.white);
				portNo.setCaretColor(null);
				portNo.setForeground(null);
				hostPaLabel.setForeground(null);
				portPaLabel.setForeground(null);
				uiDesc.setForeground(null);
				uiDesc.setBackground(null);
			}
		});
		settingsWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				hostname.setText(prefs.getHostname());
				portNo.setText(Integer.toString(prefs.getPort()));
				lineBreak.setSelected(prefs.getLineBreaking());
				darkMode.setSelected(prefs.getDarkMode());
			}

			private void storeSettings() {
				String host = hostname.getText();
				if (!prefs.getHostname().equals(host)) {
					prefs.setHostname(hostname.getText());
					reconnect = true;
				}
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
			}
		});
		settingsWindow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		settingsWindow.setResizable(false);
		settingsWindow.pack();
		settingsWindow.setLocationRelativeTo(this);
		settingsWindow.setVisible(true);
	}
	
	private Style getStyleByName(String style) {
		Style ret = term.addStyle(style, current == null ? area.getLogicalStyle() : current);
		switch (style) {
			case Commands.Styles.STYLE_NORMAL:
				ret = null;
				break;

			case Commands.Styles.STYLE_BLUE:
				StyleConstants.setForeground(ret, Color.blue);
				break;

			case Commands.Styles.STYLE_RED:
				StyleConstants.setForeground(ret, Color.red);
				break;

			case Commands.Styles.STYLE_GREEN:
				StyleConstants.setForeground(ret, Color.green);
				break;

			case Commands.Styles.STYLE_YELLOW:
				StyleConstants.setForeground(ret, Color.yellow);
				break;

			case Commands.Styles.STYLE_BLACK:
				StyleConstants.setForeground(ret, Color.black);
				break;

			case Commands.Styles.STYLE_ITALIC:
				StyleConstants.setItalic(ret, true);
				break;

			case Commands.Styles.STYLE_BOLD:
				StyleConstants.setBold(ret, true);
				break;

			case Commands.Styles.STYLE_UNDERSCORED:
				StyleConstants.setUnderline(ret, true);
				break;

			case Commands.Styles.STYLE_BG_BLACK:
				StyleConstants.setBackground(ret, Color.black);
				break;

			case Commands.Styles.STYLE_BG_BLUE:
				StyleConstants.setBackground(ret, Color.blue);
				break;

			case Commands.Styles.STYLE_BG_GREEN:
				StyleConstants.setBackground(ret, Color.green);
				break;

			case Commands.Styles.STYLE_BG_RED:
				StyleConstants.setBackground(ret, Color.red);
				break;

			case Commands.Styles.STYLE_BG_YELLOW:
				StyleConstants.setBackground(ret, Color.yellow);
				break;

			default:
				if (style.startsWith(Commands.Styles.STYLE_BG_RGB)) {
					StyleConstants.setBackground(ret, new Color(Integer.parseUnsignedInt(
							style.substring(Commands.Styles.STYLE_BG_RGB.length()), 16)));
				} else if (style.startsWith(Commands.Styles.STYLE_FG_RGB)) {
					StyleConstants.setForeground(ret, new Color(Integer.parseUnsignedInt(
							style.substring(Commands.Styles.STYLE_FG_RGB.length()), 16)));
				}
		}
		return ret;
	}

	private void parsePromptPwd(String arg) {
		pwf.setVisible(true);
		prompt.setVisible(false);
		pwf.requestFocusInWindow();
		if (arg.length() > 0)
			promptText.setText(new String(Base64.getDecoder().decode(arg), StandardCharsets.UTF_8));
		else
			promptText.setText("");
	}

	private void parsePrompt(String arg) {
		if (arg.length() > 0)
			promptText.setText(new String(Base64.getDecoder().decode(arg), StandardCharsets.UTF_8));
		else
			promptText.setText("");
	}

	private void parseEdit(String arg) {
		int i0 = arg.indexOf(Commands.Codes.SPECIAL_RAW);
		if (i0 < 0) return;
		String id = new String(Base64.getDecoder().decode(arg.substring(0, i0)), StandardCharsets.UTF_8);
		arg = arg.substring(i0 + 1);
		int i1 = arg.indexOf(Commands.Codes.SPECIAL_RAW);
		if (i1 < 0) return;
		String title = new String(Base64.getDecoder().decode(arg.substring(0, i1)), StandardCharsets.UTF_8);
		arg = new String(Base64.getDecoder().decode(arg.substring(i1 + 1)), StandardCharsets.UTF_8);
		openEditor(id, title, arg);
	}

	private void parseImg(String arg) {
		try {
			term.insertString(term.getLength(), " ", current);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		new ImageLoader(arg, term.getLength(), area,
				pane.getHeight() - pane.getHorizontalScrollBar().getHeight() - 5,
				pane.getWidth() - pane.getVerticalScrollBar().getWidth() - 5)
				.execute();
	}

	private void parseUpload(String arg) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setMultiSelectionEnabled(false);
		chooser.setDragEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			final File selected = chooser.getSelectedFile();
			if (selected.exists() && JOptionPane.showConfirmDialog(this, "File exists!\nOverwrite?",
					"File exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) {
				return;
			}
			queue.schedule(() -> {
				try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(selected))) {
					os.write(Base64.getDecoder().decode(arg));
				} catch (IOException e) {
					e.printStackTrace();
					EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(this,
							"Could not save to file!", "Save to file", JOptionPane.ERROR_MESSAGE));
				}
			}, 0, TimeUnit.NANOSECONDS);
		}
	}

	private void parseArgument(String arg) {
		int first = arg.indexOf(Commands.Codes.SPECIAL_RAW);
		if (first >= 0) {
			switch (arg.substring(0, first)) {
				case Commands.Codes.SPECIAL_PWD: parsePromptPwd(arg.substring(first + 1)); break;
				case Commands.Codes.SPECIAL_PROMPT: parsePrompt(arg.substring(first + 1)); break;
				case Commands.Codes.SPECIAL_EDIT: parseEdit(arg.substring(first + 1)); break;
				case Commands.Codes.SPECIAL_IMG: parseImg(arg); break;
				case Commands.Codes.SPECIAL_UPLOAD: parseUpload(arg.substring(first + 1)); break;
				default: current = getStyleByName(arg); break;
			}
		} else current = getStyleByName(arg);
	}

	private void showError(String text) {
		EventQueue.invokeLater(() -> {
			try {
				term.insertString(term.getLength(), text, term.getStyle(Commands.Styles.STYLE_ERROR));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		});
	}

	private boolean interpretsCommand(String command) {
		command = command.trim();
		if (command.equals(Commands.UPLOAD)) {
			final JFileChooser chooser = new JFileChooser();
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			chooser.setMultiSelectionEnabled(false);
			chooser.setDragEnabled(true);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				queue.schedule(() -> {
					ArrayList<Byte> bs = new ArrayList<>();
					try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(chooser.getSelectedFile()))) {
						int b;
						while ((b = is.read()) != -1) {
							bs.add((byte) b);
						}
					} catch (IOException e) {
						showError("Could not read file!\n");
						return;
					}
					byte[] bts = new byte[bs.size()];
					for (int i = 0; i < bs.size(); i++) {
						bts[i] = bs.get(i);
					}
					try {
						connection.send(Commands.Codes.SPECIAL_START + Commands.Codes.SPECIAL_UPLOAD + Commands.Codes.SPECIAL_RAW);
						char[] array = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
										'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
										'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
						Random r = new Random();
						StringBuilder id = new StringBuilder();
						for (int i = 0; i < 16; i++) {
							id.append(array[r.nextInt(array.length)]);
						}
						connection.send(Base64.getEncoder().encodeToString(id.toString().getBytes(StandardCharsets.UTF_8)));
						connection.send(Commands.Codes.SPECIAL_RAW);
						connection.send(Base64.getEncoder().encodeToString(bts));
						connection.send("" + Commands.Codes.SPECIAL_END);
					} catch (IOException e) {
						showError("*** Could not send message --- see console for more details! ***\n");
					}
				}, 0, TimeUnit.NANOSECONDS);
			}
			return true;
		}
		return false;
	}

	private void openEditor(String id, String path, String content) {
		ClientEditor editor = new ClientEditor(connection, id, path, content);
		editor.addWindowListener(new WindowAdapter() {
			@Override
			public void windowDeactivated(WindowEvent e) {
				prefs.setEditorDimensions(editor.getX(), editor.getY(), editor.getWidth(), editor.getHeight());
			}

			@Override
			public void windowClosed(WindowEvent e) {
				editors.remove(editor);
			}
		});
		int width = prefs.getEditorWidth();
		int height = prefs.getEditorHeight();
		if (width == -1 || height == -1) {
			editor.pack();
		} else {
			editor.setSize(width, height);
		}
		int x = prefs.getEditorPositionX();
		int y = prefs.getEditorPositionY();
		if (x == -1 || y == -1) {
			editor.setLocationRelativeTo(this);
		} else {
			if (editors.isEmpty())
				editor.setLocation(x, y);
			else
				editor.setLocation(x + 25, y + 25);
		}
		editors.add(editor);
		editor.toggleMode(prefs.getDarkMode());
		editor.setVisible(true);
	}
	
	@Override
	public void charReceived(ClientConnection connection, char c) {
		EventQueue.invokeLater(() -> {
			try {
				if (c == Commands.Codes.SPECIAL_END) {
					wasSpecial = false;
					parseArgument(buffer);
				} else if (wasSpecial)
					buffer += c;
				else if (c == Commands.Codes.SPECIAL_START) {
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
			disposeEditors();
			prompt.setText(" Connection closed. ");
			prompt.setEnabled(false);
			pwf.setVisible(false);
			prompt.setVisible(true);
			pwf.setEnabled(false);
			reconnectButton.setVisible(true);
			reconnectButton.requestFocusInWindow();
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JTextField tmp = prompt;
		switch (e.getActionCommand()) {
			case Commands.Actions.ACTION_SETTINGS:
				openSettingsWindow();
				break;

			case Commands.Actions.ACTION_RECONNECT:
				prompt.setText("");
				pwf.setText("");
				pwf.setEnabled(true);
				prompt.setEnabled(true);
				pwf.setVisible(false);
				prompt.setVisible(true);
				reconnectButton.setVisible(false);
				prompt.requestFocusInWindow();
				reconnectorHandler = queue.scheduleWithFixedDelay(reconnector, 0, 2500, TimeUnit.MILLISECONDS);
				labelTimer.restart();
				break;

			case Commands.Actions.ACTION_STATUS_LABEL_TIMER:
				connectionStatus.setVisible(false);
				break;

			case Commands.Actions.ACTION_PASSWORD:
				prompt.setVisible(true);
				pwf.setVisible(false);
				prompt.requestFocusInWindow();
				validate();
				tmp = pwf;

			case Commands.Actions.ACTION_SEND:
				// TODO Don't reset background
				String text = tmp.getText() + "\n";
				tmp.enableInputMethods(true);
				try {
					term.insertString(term.getLength(), promptText.getText(), null);
					final Style s = term.getStyle(Commands.Styles.STYLE_INPUT);
					if (tmp == prompt)
						term.insertString(term.getLength(), text, s);
					else
						term.insertString(term.getLength(), StringHelper.generateFilledString('*', text.length() - 1) + "\n", s);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				tmp.setText("");
				if (!interpretsCommand(text)) {
					queue.schedule(() -> {
						try {
							connection.send(text);
						} catch (Exception ex) {
							showError("*** Could not send message --- see console for more details! ***\n");
						}
					}, 0, TimeUnit.NANOSECONDS);
				}
				break;
		}
	}
	
	public static void openWindow(String hostname, int[] ports) {
		EventQueue.invokeLater(() -> new ClientWindow(hostname, ports).setVisible(true));
	}
}