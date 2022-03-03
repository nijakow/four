package nijakow.four.client.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

import nijakow.four.client.Commands;
import nijakow.four.client.PreferencesHelper;
import nijakow.four.client.net.ClientConnection;

public class ClientEditor extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final JScrollPane scrollPane;
	private final JCheckBox highlight;
	private final JTextPane pane;
	private final StyledDocument doc;
	private final Style def;
	private final ClientConnection connection;
	private final String id;
	private final String path;
	private final ScheduledExecutorService queue;
	private JDialog settingsWindow;
	private ScheduledFuture<?> future;
	private boolean dark;
	private final Runnable highlighter = this::updateSyntaxHighlighting;

	public ClientEditor(ClientConnection c, String id, String path, String content) {
		super(path);
		this.path = path;
		this.id = id;
		this.queue = Executors.newScheduledThreadPool(2);
		connection = c;
		pane = new JTextPane();
		pane.setText(content);
		pane.setFont(new Font("Monospaced", Font.PLAIN, 14));
		setKeyStrokes();
		doc = pane.getStyledDocument();
		doc.addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				startSyntaxHighlighting();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				startSyntaxHighlighting();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		def = pane.getLogicalStyle();
		addStyles();
		getContentPane().setLayout(new BorderLayout());
		scrollPane = new JScrollPane();
		scrollPane.setOpaque(false);
		toggleLineBreaking(PreferencesHelper.getInstance().getEditorLineBreaking());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		JButton save = new JButton("Save");
		save.addActionListener(this);
		save.setActionCommand(Commands.Actions.ACTION_EDIT_SAVE);
		JButton saveAs = new JButton("Save as...");
		saveAs.addActionListener(this);
		saveAs.setActionCommand(Commands.Actions.ACTION_EDIT_SAVE_AS);
		JButton close = new JButton("Close");
		close.addActionListener(this);
		close.setActionCommand(Commands.Actions.ACTION_EDIT_CLOSE);
		JButton settings = new JButton("Settings...");
		settings.setActionCommand(Commands.Actions.ACTION_SETTINGS);
		settings.addActionListener(this);
		highlight = new JCheckBox("Enable syntax highlighting");
		highlight.addItemListener(event -> {
			if (highlight.isSelected()) {
				startSyntaxHighlighting();
			} else {
				stopSyntaxHighlighting();
			}
		});
		if (path.endsWith(".c") || PreferencesHelper.getInstance().getEditorAlwaysHighlight()) {
			highlight.setSelected(true);
		}
		JPanel buttons = new JPanel();
		buttons.setOpaque(false);
		buttons.setLayout(new GridLayout(1, 4));
		buttons.add(close);
		buttons.add(saveAs);
		buttons.add(save);
		buttons.add(settings);
		JPanel allButtons = new JPanel();
		allButtons.setOpaque(false);
		allButtons.setLayout(new GridLayout(2, 1));
		allButtons.add(buttons);
		allButtons.add(highlight);
		getContentPane().add(allButtons, BorderLayout.SOUTH);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private void setKeyStrokes() {
		final Keymap m = pane.getKeymap();
		m.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int i;
					for (i = pane.getCaretPosition() - 1; i >= 0 && !pane.getText(i, 1).equals("\n"); i--);
					pane.setCaretPosition(i + 1);
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		});
		m.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int i;
					for (i = pane.getCaretPosition(); !pane.getText(i, 1).equals("\n"); i++);
					pane.setCaretPosition(i);
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		});
		m.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					doc.insertString(pane.getCaretPosition(), "    ", doc.getLogicalStyle(pane.getCaretPosition()));
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	private void startSyntaxHighlighting() {
		future = queue.schedule(highlighter, 0, TimeUnit.MILLISECONDS);
	}

	private  void stopSyntaxHighlighting() {
		future.cancel(false);
		doc.setCharacterAttributes(0, doc.getLength(), def, true);
	}

	public void toggleMode(boolean dark) {
		this.dark = dark;
		if (dark) {
			getContentPane().setBackground(Color.darkGray);
			highlight.setBackground(Color.darkGray);
			highlight.setForeground(Color.white);
			pane.setBackground(Color.black);
			pane.setForeground(Color.lightGray);
			pane.setCaretColor(Color.white);
		} else {
			getContentPane().setBackground(null);
			highlight.setForeground(null);
			highlight.setBackground(null);
			pane.setBackground(null);
			pane.setForeground(null);
			pane.setCaretColor(null);
		}
	}

	private void toggleLineBreaking(boolean breaking) {
		if (breaking) {
			scrollPane.setViewportView(pane);
		} else {
			JPanel wrap = new JPanel();
			wrap.setOpaque(false);
			wrap.setLayout(new BorderLayout());
			wrap.add(pane);
			scrollPane.setViewportView(wrap);
		}
	}

	private JDialog createSettingsWindow() {
		JDialog settingsWindow = new JDialog(this, "Editor: Settings", true);
		settingsWindow.getContentPane().setLayout(new GridLayout(2, 1));
		JCheckBox alwaysHighlight = new JCheckBox("Always enable syntax highlighting");
		alwaysHighlight.addItemListener(event -> PreferencesHelper.getInstance().setEditorAlwaysHighlight(alwaysHighlight.isSelected()));
		settingsWindow.getContentPane().add(alwaysHighlight);
		JCheckBox lineBreaking = new JCheckBox("Automatic line breaking");
		lineBreaking.addItemListener(event -> {
			boolean  breaking = lineBreaking.isSelected();
			toggleLineBreaking(breaking);
			PreferencesHelper.getInstance().setEditorLineBreaking(breaking);
		});
		settingsWindow.getContentPane().add(lineBreaking);
		settingsWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				alwaysHighlight.setSelected(PreferencesHelper.getInstance().getEditorAlwaysHighlight());
				lineBreaking.setSelected(PreferencesHelper.getInstance().getEditorLineBreaking());
				if (dark) {
					settingsWindow.getContentPane().setBackground(Color.darkGray);
					alwaysHighlight.setBackground(Color.darkGray);
					alwaysHighlight.setForeground(Color.white);
					lineBreaking.setBackground(Color.darkGray);
					lineBreaking.setForeground(Color.white);
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
				PreferencesHelper.getInstance().flush();
			}
		});
		settingsWindow.pack();
		settingsWindow.setResizable(false);
		settingsWindow.setDefaultCloseOperation(HIDE_ON_CLOSE);
		settingsWindow.setLocationRelativeTo(this);
		return settingsWindow;
	}

	private void showSettingsWindow() {
		if (settingsWindow == null) {
			settingsWindow = createSettingsWindow();
		}
		settingsWindow.setVisible(true);
	}

	@Override
	public void dispose() {
		send(false, null);
		settingsWindow.dispose();
		super.dispose();
	}

	private void addStyles() {
		Style s = doc.addStyle(Commands.Styles.STYLE_KEYWORD, def);
		StyleConstants.setForeground(s, Color.red);
		StyleConstants.setBold(s, true);
		s = doc.addStyle(Commands.Styles.STYLE_TYPE, def);
		StyleConstants.setForeground(s, Color.green);
		s = doc.addStyle(Commands.Styles.STYLE_SPECIAL_WORD, def);
		StyleConstants.setItalic(s, true);
		StyleConstants.setForeground(s, Color.blue);
		s = doc.addStyle(Commands.Styles.STYLE_STDLIB, def);
		StyleConstants.setForeground(s, Color.orange);
		s= doc.addStyle(Commands.Styles.STYLE_COMMENT, def);
		StyleConstants.setForeground(s, Color.gray);
	}
	
	private void updateSyntaxHighlighting() {
		Style defaultStyle = StyleContext.
				   getDefaultStyleContext().
				   getStyle(StyleContext.DEFAULT_STYLE);
		doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);
		String keywords = "\\b(new|struct|class|inherits|use|this|if|else|while|for|break|continue|switch|case|return|private|protected|public)\\b";
		Matcher matcher = Pattern.compile(keywords).matcher(pane.getText());
		while (matcher.find())
			doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
					doc.getStyle(Commands.Styles.STYLE_KEYWORD), true);
		keywords = "\\b(any|void|int|char|bool|string|object|list|mapping)\\b";
		matcher = Pattern.compile(keywords).matcher(pane.getText());
		while (matcher.find())
			doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
					doc.getStyle(Commands.Styles.STYLE_TYPE), true);
		keywords = "\\b(true|false|nil|va_next|va_count)\\b";
		matcher = Pattern.compile(keywords).matcher(pane.getText());
		while (matcher.find())
			doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
					doc.getStyle(Commands.Styles.STYLE_SPECIAL_WORD), true);
		keywords = "\\b(create|the|call|log|length|insert|append|remove|strlen|chr|write|prompt|password|edit)\\b";
		matcher = Pattern.compile(keywords).matcher(pane.getText());
		while (matcher.find())
			doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
					doc.getStyle(Commands.Styles.STYLE_STDLIB), true);
		keywords = "/\\*(.*)\\*/";
		matcher = Pattern.compile(keywords).matcher(pane.getText());
		while (matcher.find())
			doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
					doc.getStyle(Commands.Styles.STYLE_COMMENT), true);
	}

	public void send(final boolean save, final String newPath) {
		// TODO filter escape characters
		queue.execute(() -> {
			try {
				connection.send(Commands.Codes.SPECIAL_START + Commands.Codes.SPECIAL_EDIT + id);
				if (newPath != null)
					connection.send(Commands.Codes.SPECIAL_RAW + newPath);
				if (save)
					connection.send(Commands.Codes.SPECIAL_RAW + pane.getText());
				connection.send("" + Commands.Codes.SPECIAL_END);
			} catch (IOException e) {
				System.err.println("Could not send message!");
			}
		});
	}

	private void saveAs() {
		String input = JOptionPane.showInputDialog(this,
				"Enter the new file name:",
				"Save as...", JOptionPane.PLAIN_MESSAGE);
		if (input != null) {
			JOptionPane.showMessageDialog(this, "This feature might not be implemented yet!",
					"Hic sunt dracones!", JOptionPane.WARNING_MESSAGE);
			send(true, input);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case Commands.Actions.ACTION_EDIT_SAVE: send(true, null); break;

			case Commands.Actions.ACTION_EDIT_SAVE_AS: saveAs(); break;

			case Commands.Actions.ACTION_SETTINGS: showSettingsWindow(); break;

			case Commands.Actions.ACTION_EDIT_CLOSE: dispose(); break;
		}
	}
}