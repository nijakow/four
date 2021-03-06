package nijakow.four.client;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
import javax.swing.text.StyledDocument;

import nijakow.four.client.editor.*;
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
	private final JTextPane smalltalk;
	private final JPanel mainPanel;
	private final StyledDocument term;
	private final FDocument smallSyntaxDoc;
	private final Style defaultStyle;
	private final FStyle errorStyle;
	private final FStyle inputStyle;
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
	private boolean wasAnsi;
	private boolean editorShowing;
	private FStyle current;
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
		super(Commands.Strings.TITLE);
		final Font font = new Font("Monospaced", Font.PLAIN, 14);
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
		if (ports.length == 1)
			prefs.setPort(ports[0]);
		portCounter = 0;
		mainPanel = new JPanel(new BorderLayout());
		JPanel south = new JPanel();
		south.setOpaque(false);
		south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
		smalltalk = new JTextPane();
		smalltalk.setFont(new Font("Monospaced", Font.PLAIN, 14));
		smallSyntaxDoc = new FDocument();
		smallSyntaxDoc.setAutoIndentingEnabled(true);
		smallSyntaxDoc.setHighlightingEnabled(true);
		updateSyntaxTheme();
		errorStyle = new FStyle();
		errorStyle.setBold(true);
		errorStyle.setItalic(true);
		errorStyle.setForeground(Color.red);
		inputStyle = new FStyle();
		inputStyle.setForeground(Color.gray);
		smalltalk.setDocument(smallSyntaxDoc);
		smalltalk.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				smalltalk.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, prefs.getShiftForNewline() ? 0 : KeyEvent.SHIFT_DOWN_MASK), new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						final String content = smalltalk.getText();
						try {
							final int length = promptText.getText().length();
							term.insertString(term.getLength(), promptText.getText(), null);
							for (int i = 0; i < content.length(); i++) {
								smalltalk.setCaretPosition(i);
								term.insertString(term.getLength(), "" + content.charAt(i), smalltalk.getCharacterAttributes());
								if (content.charAt(i) == '\n') {
									term.insertString(term.getLength(), StringHelper.generateFilledString(' ', length),
											inputStyle.asStyle(defaultStyle));
								}
							}
							term.insertString(term.getLength(), "\n", null);
						} catch (BadLocationException ex) {
							ex.printStackTrace();
						}
						queue.schedule(() -> sendSmalltalk(content), 0, TimeUnit.NANOSECONDS);
						smalltalk.setText("");
					}
				});
				smalltalk.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, prefs.getShiftForNewline() ? KeyEvent.SHIFT_DOWN_MASK : 0), new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							smallSyntaxDoc.insertString(smalltalk.getCaretPosition(), "\n", null);
						} catch (BadLocationException ex) {
							assert (false);
						}
					}
				});
			}

			@Override
			public void focusLost(FocusEvent e) {
				smalltalk.getKeymap().removeKeyStrokeBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
				smalltalk.getKeymap().removeKeyStrokeBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.SHIFT_DOWN_MASK));
			}
		});
		smalltalk.setVisible(false);
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
		mainPanel.add(connectionStatus, BorderLayout.NORTH);
		JButton settings = new JButton("Settings");
		settings.addActionListener(this);
		settings.setActionCommand(Commands.Actions.ACTION_SETTINGS);
		south.add(promptText);
		south.add(prompt);
		south.add(pwf);
		south.add(smalltalk);
		south.add(reconnectButton);
		reconnectButton.setVisible(false);
		pwf.setVisible(false);
		south.add(settings);
		mainPanel.add(south, BorderLayout.SOUTH);
		area = new JTextPane();
		defaultStyle = area.getLogicalStyle();          // TODO: Default style should be adjustable.
		current = new FStyle();
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
		pane = new JScrollPane();
		pane.setOpaque(false);
		setLineBreaking(prefs.getLineBreaking());
		mainPanel.add(pane, BorderLayout.CENTER);
		getContentPane().add(mainPanel);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		int x = prefs.getWindowPositionX();
		int y = prefs.getWindowPositionY();
		if (x == -1 || y == -1)
			setLocationRelativeTo(null);
		else
			setLocation(x, y);
		int width = prefs.getWindowWidth();
		int height = prefs.getWindowHeight();
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

	private void updateSyntaxTheme() {
		final String theme = prefs.getEditorTheme();
		if (!theme.equals(Commands.Themes.DEFAULT)) {
			queue.schedule(() -> {
				try {
					smallSyntaxDoc.setTheme(new GenericTheme(new File(theme)));
				} catch (IOException | GenericTheme.ParseException e) {
					JOptionPane.showMessageDialog(this, "Could not open theme file: " + theme + "!\n" +
									(e instanceof GenericTheme.ParseException ? ((GenericTheme.ParseException) e).getErrorText() : "") +
                             "\nSwitching to default theme...",
							"File error", JOptionPane.ERROR_MESSAGE);
					smallSyntaxDoc.setTheme(null);
					prefs.setEditorTheme(Commands.Themes.DEFAULT);
				}
			}, 0, TimeUnit.NANOSECONDS);
		} else if (!(smallSyntaxDoc.getTheme() instanceof DefaultTheme)) {
			smallSyntaxDoc.setTheme(null);
		}
		// FIXME Still not linked correctly!
	}

	private void toggleMode(boolean dark) {
		if (dark) {
			pwf.setForeground(Color.white);
			pwf.setBackground(Color.gray);
			pwf.setCaretColor(Color.white);
			area.setForeground(Color.lightGray);
			area.setBackground(Color.black);
			smalltalk.setForeground(Color.white);
			smalltalk.setBackground(Color.black);
			smalltalk.setCaretColor(Color.white);
			prompt.setForeground(Color.white);
			prompt.setBackground(Color.gray);
			prompt.setCaretColor(Color.white);
			promptText.setForeground(Color.white);
			promptText.setBackground(Color.darkGray);
			connectionStatus.setBackground(Color.darkGray);
			mainPanel.setBackground(Color.darkGray);
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
			smalltalk.setForeground(null);
			smalltalk.setBackground(Color.white);
			smalltalk.setCaretColor(Color.black);
			promptText.setForeground(null);
			promptText.setBackground(null);
			connectionStatus.setBackground(null);
			mainPanel.setBackground(null);
			getContentPane().setBackground(null);
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

	@Override
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
		JCheckBox shiftForNewline = new JCheckBox("Accept Smalltalk messages with Enter");
		shiftForNewline.addItemListener(event -> prefs.setShiftForNewline(shiftForNewline.isSelected()));
		JCheckBox darkMode = new JCheckBox("Dark mode");
		settingsWindow.getContentPane().add(darkMode);
		settingsWindow.getContentPane().add(lineBreak);
		settingsWindow.getContentPane().add(shiftForNewline);
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
				shiftForNewline.setForeground(Color.white);
				shiftForNewline.setBackground(Color.darkGray);
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
				shiftForNewline.setForeground(null);
				shiftForNewline.setBackground(null);
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
				shiftForNewline.setSelected(prefs.getShiftForNewline());
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

	private void alterCurrentStyleByName(String style) {
		switch (style) {
			case Commands.Styles.STYLE_NORMAL:      current = new FStyle();             break;
			case Commands.Styles.STYLE_ITALIC:      current.setItalic(true);            break;
			case Commands.Styles.STYLE_BOLD:        current.setBold(true);              break;
			case Commands.Styles.STYLE_UNDERSCORED: current.setUnderlined(true);        break;
			default:
				if (style.startsWith(Commands.Styles.STYLE_BG_RGB)) {
					current.setBackground(new Color(Integer.parseUnsignedInt(
							style.substring(Commands.Styles.STYLE_BG_RGB.length()), 16)));
				} else if (style.startsWith(Commands.Styles.STYLE_FG_RGB)) {
					current.setForeground(new Color(Integer.parseUnsignedInt(
							style.substring(Commands.Styles.STYLE_FG_RGB.length()), 16)));
				}
				break;
		}
	}

	private void parsePromptPwd(String arg) {
		pwf.setVisible(true);
		prompt.setVisible(false);
		smalltalk.setVisible(false);
		pwf.requestFocusInWindow();
		if (arg.length() > 0)
			promptText.setText(new String(Base64.getDecoder().decode(arg), StandardCharsets.UTF_8));
		else
			promptText.setText("");
	}

	private void parsePrompt(String arg) {
		prompt.setVisible(true);
		smalltalk.setVisible(false);
		prompt.requestFocusInWindow();
		if (arg.length() > 0)
			promptText.setText(new String(Base64.getDecoder().decode(arg), StandardCharsets.UTF_8));
		else
			promptText.setText("");
	}

	private void sendSmalltalk(String text) {
		try {
			connection.send(Commands.Codes.SPECIAL_START + "");
			connection.send(Commands.Codes.SPECIAL_LINE_SMALLTALK + Commands.Codes.SPECIAL_RAW);
			connection.send(Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)));
			connection.send("" + Commands.Codes.SPECIAL_END);
		} catch (IOException e) {
			showError("*** Could not send message --- see console for more details! ***\n");
		}
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
			term.insertString(term.getLength(), " ", current.asStyle(defaultStyle));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		new ImageLoader(arg, term.getLength(), area,
				pane.getHeight() - pane.getHorizontalScrollBar().getHeight() - 5,
				pane.getWidth() - pane.getVerticalScrollBar().getWidth() - 5)
				.execute();
	}

	private void parseDownload(String arg) {
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
				try (BufferedOutputStream os = new BufferedOutputStream(Files.newOutputStream(selected.toPath()))) {
					os.write(Base64.getDecoder().decode(arg));
				} catch (IOException e) {
					e.printStackTrace();
					EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(this,
							"Could not save to file!", "Save to file", JOptionPane.ERROR_MESSAGE));
				}
			}, 0, TimeUnit.NANOSECONDS);
		}
	}

	private void parsePromptSmalltalk(String arg) {
		pwf.setVisible(false);
		prompt.setVisible(false);
		smalltalk.setVisible(true);
		smalltalk.requestFocusInWindow();
		if (arg.length() > 0) {
			int i = arg.indexOf(Commands.Codes.SPECIAL_RAW);
			if (i < 0)
				promptText.setText(new String(Base64.getDecoder().decode(arg), StandardCharsets.UTF_8));
			else {
				promptText.setText(new String(Base64.getDecoder().decode(arg.substring(0, i)), StandardCharsets.UTF_8));
				smalltalk.setText(new String(Base64.getDecoder().decode(arg.substring(i + 1)), StandardCharsets.UTF_8));
			}
		} else {
			promptText.setText("");
		}
	}

	private void parseArgument(String arg) {
		int first = arg.indexOf(Commands.Codes.SPECIAL_RAW);
		if (first >= 0) {
			switch (arg.substring(0, first)) {
				case Commands.Codes.SPECIAL_PWD:       parsePromptPwd(arg.substring(first + 1));       break;
				case Commands.Codes.SPECIAL_PROMPT:    parsePrompt(arg.substring(first + 1));          break;
				case Commands.Codes.SPECIAL_EDIT:      parseEdit(arg.substring(first + 1));            break;
				case Commands.Codes.SPECIAL_IMG:       parseImg(arg);                                             break;
				case Commands.Codes.SPECIAL_UPLOAD:    parseDownload(arg.substring(first + 1));        break;
				case Commands.Codes.SPECIAL_DOWNLOAD:  performUpload(arg.substring(first + 1));        break;
				case Commands.Codes.SPECIAL_SMALLTALK: parsePromptSmalltalk(arg.substring(first + 1)); break;
				default: alterCurrentStyleByName(arg); break;
			}
		} else alterCurrentStyleByName(arg);
	}

	private List<Integer> splitAnsiToArgs(String arg) {
		ArrayList<Integer> ret = new ArrayList<>();
		arg = arg.substring(1);
		String[] splits = arg.split(";");
		for (String s : splits) {
			ret.add(Integer.decode(s));
		}
		return ret;
	}

	private void set256ForegroundColour(int colourCode) {
		System.err.println(colourCode + ": 256 colour table not available!");
	}

	private void set256BackgroundColour(int colourCode) {
		System.err.println(colourCode + ": 256 colour table not available!");
	}

	private void parseAnsi(String arg) {
		List<Integer> args = splitAnsiToArgs(arg);
		for (int i = 0; i < args.size(); ++i) {
			switch (args.get(i)) {
				case 0:   current = new FStyle();                 break;
				case 1:   current.setBold(true);                  break;
				case 4:   current.setUnderlined(true);            break;
				case 21:  current.setBold(false);                 break;
				case 24:  current.setUnderlined(false);           break;

				// Foreground
				case 30:  current.setForeground(Color.black);     break;
				case 91:                                                 // Not supported: light red
				case 31:  current.setForeground(Color.red);       break;
				case 92:                                                 // Not supported: light green
				case 32:  current.setForeground(Color.green);     break;
				case 93:                                                 // Not supported: light yellow
				case 33:  current.setForeground(Color.yellow);    break;
				case 94:                                                 // Not supported: light blue
				case 34:  current.setForeground(Color.blue);      break;
				case 95:                                                 // Not supported: light magenta
				case 35:  current.setForeground(Color.magenta);   break;
				case 96:                                                 // Not supported: light cyan
				case 36:  current.setForeground(Color.cyan);      break;
				case 37:  current.setForeground(Color.lightGray); break;
				case 39:  current.setForeground(null);            break;
				case 90:  current.setForeground(Color.darkGray);  break;
				case 97:  current.setForeground(Color.white);     break;

				// Background
				case 40:  current.setBackground(Color.black);     break;
				case 101:                                                // Not supported: light red
				case 41:  current.setBackground(Color.red);       break;
				case 102:                                                // Not supported: light green
				case 42:  current.setBackground(Color.green);     break;
				case 103:                                                // Not supported: light yellow
				case 43:  current.setBackground(Color.yellow);    break;
				case 104:                                                // Not supported: light blue
				case 44:  current.setBackground(Color.blue);      break;
				case 105:                                                // Not supported: light magenta
				case 45:  current.setBackground(Color.magenta);   break;
				case 106:                                                // Not supported: light cyan
				case 46:  current.setBackground(Color.cyan);      break;
				case 47:  current.setBackground(Color.lightGray); break;
				case 49:  current.setBackground(null);            break;
				case 100: current.setBackground(Color.darkGray);  break;
				case 107: current.setBackground(Color.white);     break;

				// 256 colour support and RGB colours
				case 38:
					++i;
					if (args.get(i) == 5) set256ForegroundColour(args.get(i + 1)); // <-- Currently not supported!
					else if (args.get(i) == 2) {
						current.setForeground(new Color(args.get(i + 1), args.get(i + 2), args.get(i + 3)));
						i += 2;
					}
					break;

				case 48:
					++i;
					if (args.get(i) == 5) set256BackgroundColour(args.get(i + 1));  // <-- Currently not supported!
					else if (args.get(i) == 2) {
						current.setBackground(new Color(args.get(i + 1), args.get(i + 2), args.get(i + 3)));
						i += 2;
					}
					break;
			}
		}
	}

	private void showError(String text) {
		EventQueue.invokeLater(() -> {
			try {
				term.insertString(term.getLength(), text, errorStyle.asStyle(defaultStyle));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		});
	}

	private void loadAndSend(String key, File file) {
		ArrayList<Byte> bs = new ArrayList<>();
		try (BufferedInputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
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
			connection.send(key);
			connection.send(Commands.Codes.SPECIAL_RAW);
			connection.send(Base64.getEncoder().encodeToString(bts));
			connection.send("" + Commands.Codes.SPECIAL_END);
		} catch (IOException e) {
			showError("*** Could not send message --- see console for more details! ***\n");
		}
	}

	private void performUpload(final String key) {
		final JDialog dialog = new JDialog(this, "Upload files", false);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel("Drop files here or click to select", SwingConstants.CENTER);
		panel.add(label, BorderLayout.SOUTH);
		panel.setTransferHandler(new TransferHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public boolean importData(TransferSupport support) {
				try {
					List<File> list = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					for (File f : list) {
						queue.schedule(() -> loadAndSend(key, f), 0, TimeUnit.NANOSECONDS);
					}
				} catch (UnsupportedFlavorException | IOException e) {
					return false;
				}
				dialog.dispose();
				return true;
			}

			@Override
			public boolean canImport(TransferSupport support) {
				support.setDropAction(COPY);
				return true;
			}
		});
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				final JFileChooser chooser = new JFileChooser();
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setMultiSelectionEnabled(false);
				chooser.setDragEnabled(true);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
					queue.schedule(() -> loadAndSend(key, chooser.getSelectedFile()), 0, TimeUnit.NANOSECONDS);
					dialog.dispose();
				}
			}
		});
		dialog.getContentPane().add(panel);
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				if (prefs.getDarkMode()) {
					dialog.getContentPane().setBackground(Color.darkGray);
					panel.setBackground(Color.darkGray);
					label.setBackground(Color.darkGray);
					label.setForeground(Color.white);
				} else {
					dialog.getContentPane().setBackground(null);
					panel.setBackground(null);
					label.setBackground(null);
					label.setForeground(null);
				}
			}
		});
		dialog.setSize(250, 250);
		dialog.setLocationRelativeTo(this);
		dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

	private void switchToMainView(String oldTitle) {
		invalidate();
		getContentPane().removeAll();
		getContentPane().add(mainPanel);
		setTitle(oldTitle);
		updateSyntaxTheme();
		validate();
		repaint();
		if (smalltalk.isVisible()) {
			smalltalk.requestFocusInWindow();
		} else if (prompt.isVisible()) {
			prompt.requestFocusInWindow();
		}
	}

	private void openEditor(String id, String title, String content) {
		invalidate();
		ClientEditor editor = new ClientEditor(connection, id, content, this);
		final String oldTitle = getTitle();
		setTitle(title);
		getContentPane().removeAll();
		getContentPane().add(editor);
		editor.toggleMode(prefs.getDarkMode());
		editor.setCallback(() -> {
			switchToMainView(oldTitle);
			editorShowing = false;
		});
		validate();
		repaint();
		editor.requestFocusInWindow();
		editorShowing = true;
	}

	@Override
	public void charReceived(ClientConnection connection, char c) {
		EventQueue.invokeLater(() -> {
			try {
				if (c == Commands.Codes.ANSI_END && wasAnsi) {
					wasAnsi = false;
					parseAnsi(buffer);
				} else if (wasAnsi) {
					buffer += c;
				} else if (c == Commands.Codes.ANSI_START) {
					wasAnsi = true;
					buffer = "";
				} else if (c == Commands.Codes.SPECIAL_END) {
					wasSpecial = false;
					parseArgument(buffer);
				} else if (wasSpecial)
					buffer += c;
				else if (c == Commands.Codes.SPECIAL_START) {
					wasSpecial = true;
					buffer = "";
				}
				else
					term.insertString(term.getLength(), Character.toString(c), current.asStyle(defaultStyle));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void connectionLost(ClientConnection connection) {
		EventQueue.invokeLater(() -> {
			if (editorShowing) switchToMainView(Commands.Strings.TITLE);
			prompt.setText(" Connection closed. ");
			prompt.setEnabled(false);
			pwf.setVisible(false);
			smalltalk.setVisible(false);
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
					if (tmp == prompt)
						term.insertString(term.getLength(), text, inputStyle.asStyle(defaultStyle));
					else
						term.insertString(term.getLength(), StringHelper.generateFilledString('*', text.length() - 1) + "\n", inputStyle.asStyle(defaultStyle));
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
				tmp.setText("");
				queue.schedule(() -> {
					try {
						connection.send(text);
					} catch (Exception ex) {
						showError("*** Could not send message --- see console for more details! ***\n");
					}
				}, 0, TimeUnit.NANOSECONDS);
				break;
		}
	}

	public static void openWindow(String hostname, int[] ports) {
		EventQueue.invokeLater(() -> new ClientWindow(hostname, ports).setVisible(true));
	}
}