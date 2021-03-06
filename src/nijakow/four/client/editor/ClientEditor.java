package nijakow.four.client.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.*;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.*;

import nijakow.four.client.Commands;
import nijakow.four.client.PreferencesHelper;
import nijakow.four.client.net.ClientConnection;

public class ClientEditor extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_THEME = "Default";
	private static final String OTHER_THEME = "Other...";
	private final JCheckBox highlight;
	private final JScrollPane scrollPane;
	private final JTextPane pane;
	private final FSuggestionMenu popup;
	private final FDocument doc;
	private final ClientConnection connection;
	private final String id;
	private final ScheduledExecutorService queue;
	private final FStyle def;
	private final Frame parent;
	private JDialog settingsWindow;
	private Runnable callback;
	private boolean dark;

	public ClientEditor(ClientConnection c, String id, String content, Frame parent) {
		super(new BorderLayout());
		this.parent = parent;
		this.id = id;
		queue = Executors.newSingleThreadScheduledExecutor();
		connection = c;
		pane = new JTextPane();
		doc = new FDocument();
		doc.setAutoIndentingEnabled(PreferencesHelper.getInstance().getAutoIndenting());
		doc.setParsingEnabled(true);
		String theme = PreferencesHelper.getInstance().getEditorTheme();
		EventQueue.invokeLater(() -> {
			if (!theme.equals(Commands.Themes.DEFAULT)) {
				try {
					doc.setTheme(new GenericTheme(new File(theme)));
					if (doc.isSyntaxHighlighting()) doc.updateSyntaxHighlighting();
				} catch (IOException | GenericTheme.ParseException e) {
					JOptionPane.showMessageDialog(this, "Could not open theme file: " + theme + "!\n" +
									(e instanceof GenericTheme.ParseException ? ((GenericTheme.ParseException) e).getErrorText() : "") +
							"\nSwitching to default theme...",
							"File error", JOptionPane.ERROR_MESSAGE);
					PreferencesHelper.getInstance().setEditorTheme(Commands.Themes.DEFAULT);
				}
			}
		});
		pane.setDocument(doc);
		pane.setText(content);
		pane.setFont(new Font("Monospaced", Font.PLAIN, 14));
		def = new FStyle(pane.getLogicalStyle());
		popup = new FSuggestionMenu();
		popup.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				addPopupKeyStrokes();
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				removePopupKeyStrokes();
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
		setKeyStrokes();
		scrollPane = new JScrollPane();
		scrollPane.setOpaque(false);
		toggleLineBreaking(PreferencesHelper.getInstance().getEditorLineBreaking());
		add(scrollPane, BorderLayout.CENTER);
		JButton save = new JButton("Save");
		save.addActionListener(this);
		save.setActionCommand(Commands.Actions.ACTION_EDIT_SAVE);
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
		if (PreferencesHelper.getInstance().getEditorAlwaysHighlight()) {
			highlight.setSelected(true);
		}
		JPanel buttons = new JPanel();
		buttons.setOpaque(false);
		buttons.setLayout(new GridLayout(1, 3));
		buttons.add(close);
		buttons.add(save);
		buttons.add(settings);
		JPanel allButtons = new JPanel();
		allButtons.setOpaque(false);
		allButtons.setLayout(new GridLayout(2, 1));
		allButtons.add(buttons);
		allButtons.add(highlight);
		add(allButtons, BorderLayout.SOUTH);
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				setKeyStrokes();
			}

			@Override
			public void focusLost(FocusEvent e) {
				popup.setVisible(false);
			}
		});
	}

	private void setKeyStrokes() {
		final Keymap m = pane.getKeymap();
		m.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pane.setCaretPosition(doc.getLineStart(pane.getCaretPosition()));
			}
		});
		m.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pane.setCaretPosition(doc.getLineEnd(pane.getCaretPosition()));
				if (popup.isVisible()) popup.setVisible(false);
			}
		});
		m.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popup.setVisible(false);
			}
		});
		m.addActionForKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_DOWN_MASK), new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!popup.isVisible()) {
					popup.removeAll();
					popup.addAll(doc.computeSuggestions(pane.getCaretPosition()));
					popup.selectNext();
					Point panePos = pane.getLocationOnScreen();
					Point caretPos = pane.getCaret().getMagicCaretPosition();
					popup.setLocation(caretPos.x + panePos.x, caretPos.y + panePos.y + pane.getFont().getSize());
				}
				popup.setVisible(!popup.isVisible());
			}
		});
	}

	private void addPopupKeyStrokes() {
		final Keymap m = pane.getKeymap();
		m.addActionForKeyStroke(Commands.Keys.LEFT, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int currentPosition = pane.getCaretPosition();
				pane.setCaretPosition(currentPosition == 0 ? 0 : currentPosition - 1);
				if (popup.isVisible()) popup.setVisible(false);
			}
		});
		m.addActionForKeyStroke(Commands.Keys.RIGHT, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int currentPosition = pane.getCaretPosition();
				pane.setCaretPosition(currentPosition == doc.getLength() ? currentPosition : currentPosition + 1);
				if (popup.isVisible()) popup.setVisible(false);
			}
		});
		m.addActionForKeyStroke(Commands.Keys.UP, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popup.selectPrevious();
			}
		});
		m.addActionForKeyStroke(Commands.Keys.DOWN, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popup.selectNext();
			}
		});
		m.addActionForKeyStroke(Commands.Keys.ENTER, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					doc.insertString(pane.getCaretPosition(), popup.getSelection(), null);
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
				popup.setVisible(false);
			}
		});
	}

	private void removePopupKeyStrokes() {
		final Keymap m = pane.getKeymap();
		m.removeKeyStrokeBinding(Commands.Keys.DOWN);
		m.removeKeyStrokeBinding(Commands.Keys.LEFT);
		m.removeKeyStrokeBinding(Commands.Keys.UP);
		m.removeKeyStrokeBinding(Commands.Keys.RIGHT);
		m.removeKeyStrokeBinding(Commands.Keys.ENTER);
	}

	private void startSyntaxHighlighting() {
		doc.setHighlightingEnabled(true);
	}

	private void stopSyntaxHighlighting() {
		doc.setHighlightingEnabled(false);
	}

	public void toggleMode(boolean dark) {
		this.dark = dark;
		if (dark) {
			setBackground(Color.darkGray);
			highlight.setBackground(Color.darkGray);
			highlight.setForeground(Color.white);
			pane.setBackground(Color.black);
			pane.setForeground(Color.lightGray);
			pane.setCaretColor(Color.white);
		} else {
			setBackground(null);
			highlight.setForeground(null);
			highlight.setBackground(null);
			pane.setBackground(Color.white);
			pane.setForeground(null);
			pane.setCaretColor(Color.black);
		}
		if (doc.isSyntaxHighlighting()) doc.updateSyntaxHighlighting();
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

	private String chooseTheme() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDragEnabled(true);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File selected = chooser.getSelectedFile();
			try {
				doc.setTheme(new GenericTheme(selected));
			} catch (GenericTheme.ParseException e) {
				JOptionPane.showMessageDialog(this, e.getErrorText(), "Could not parse file!", JOptionPane.ERROR_MESSAGE);
				return null;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Could not open file: " + selected + "!",
						"File error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return null;
			}
			return selected.getAbsolutePath();
		}
		return null;
	}

	private JDialog createSettingsWindow() {
		JDialog settingsWindow = new JDialog(parent, "Editor: Settings", true);
		settingsWindow.getContentPane().setLayout(new BorderLayout());
		JPanel themePanel = new JPanel(new GridLayout(3, 1));
		JButton themeChoose = new JButton("Open from disc...");
		JButton themeCreate = new JButton("Create...");
		JPanel themeButtons = new JPanel(new GridLayout(1, 2));
		themeButtons.add(themeChoose);
		themeButtons.add(themeCreate);
		JComboBox<String> comboBox = new JComboBox<>();
		comboBox.setEditable(false);
		comboBox.addItem(DEFAULT_THEME);
		String setTheme = PreferencesHelper.getInstance().getEditorTheme();
		if (!setTheme.equals(Commands.Themes.DEFAULT)) {
			comboBox.addItem(setTheme);
			comboBox.setSelectedItem(setTheme);
			themeCreate.setText("Edit...");
			themeChoose.setVisible(false);
		} else {
			comboBox.setSelectedItem(DEFAULT_THEME);
			themeCreate.setText("Copy and edit...");
			themeChoose.setVisible(false);
		}
		comboBox.addItem(OTHER_THEME);
		comboBox.addItemListener(event -> {
			String selected = (String) comboBox.getSelectedItem();
			if (selected != null) {
				switch (selected) {
					case OTHER_THEME:
						themeChoose.setVisible(true);
						themeCreate.setVisible(true);
						themeCreate.setText("Create...");
						break;

					case DEFAULT_THEME:
						themeChoose.setVisible(false);
						themeCreate.setVisible(true);
						themeCreate.setText("Copy and edit...");
						PreferencesHelper.getInstance().setEditorTheme(Commands.Themes.DEFAULT);
						doc.setTheme(null);
						break;

					default:
						themeChoose.setVisible(false);
						themeCreate.setVisible(true);
						themeCreate.setText("Edit...");
						try {
							File selectedTheme = new File(selected);
							doc.setTheme(new GenericTheme(selectedTheme));
							PreferencesHelper.getInstance().setEditorTheme(selectedTheme.getAbsolutePath());
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
				}
				if (doc.isSyntaxHighlighting()) doc.updateSyntaxHighlighting();
			}
		});
		JLabel themeDesc = new JLabel("Choose the used theme:");
		themeChoose.addActionListener(event -> {
			String path = chooseTheme();
			if (path != null) {
				comboBox.addItem(path);
				comboBox.setSelectedItem(path);
				PreferencesHelper.getInstance().setEditorTheme(path);
			}
		});
		themeCreate.addActionListener(event -> {
			if (comboBox.getSelectedItem().equals(OTHER_THEME)) {
				showThemeEditor(null);
			} else {
				showThemeEditor(doc.getTheme(), (String) comboBox.getSelectedItem());
			}
		});
		themePanel.add(themeDesc);
		themePanel.add(comboBox);
		themePanel.add(themeButtons);
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1));
		JCheckBox alwaysHighlight = new JCheckBox("Always enable syntax highlighting");
		alwaysHighlight.addItemListener(event -> PreferencesHelper.getInstance().setEditorAlwaysHighlight(alwaysHighlight.isSelected()));
		panel.add(alwaysHighlight);
		JCheckBox lineBreaking = new JCheckBox("Automatic line breaking");
		lineBreaking.addItemListener(event -> {
			boolean breaking = lineBreaking.isSelected();
			toggleLineBreaking(breaking);
			PreferencesHelper.getInstance().setEditorLineBreaking(breaking);
		});
		panel.add(lineBreaking);
		JCheckBox autoIndent = new JCheckBox("Enable auto-indenting");
		autoIndent.addItemListener(event -> {
			boolean indent = autoIndent.isSelected();
			doc.setAutoIndentingEnabled(indent);
			PreferencesHelper.getInstance().setAutoIndenting(indent);
		});
		panel.add(autoIndent);
		settingsWindow.getContentPane().add(themePanel, BorderLayout.CENTER);
		settingsWindow.getContentPane().add(panel, BorderLayout.SOUTH);
		settingsWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				alwaysHighlight.setSelected(PreferencesHelper.getInstance().getEditorAlwaysHighlight());
				autoIndent.setSelected(PreferencesHelper.getInstance().getAutoIndenting());
				lineBreaking.setSelected(PreferencesHelper.getInstance().getEditorLineBreaking());
				if (dark) {
					settingsWindow.getContentPane().setBackground(Color.darkGray);
					alwaysHighlight.setBackground(Color.darkGray);
					alwaysHighlight.setForeground(Color.white);
					autoIndent.setBackground(Color.darkGray);
					autoIndent.setForeground(Color.white);
					lineBreaking.setBackground(Color.darkGray);
					lineBreaking.setForeground(Color.white);
					panel.setBackground(Color.darkGray);
					themePanel.setBackground(Color.darkGray);
					themeDesc.setForeground(Color.white);
					themeDesc.setBackground(Color.darkGray);
					themeButtons.setBackground(Color.darkGray);
				} else {
					settingsWindow.getContentPane().setBackground(null);
					alwaysHighlight.setBackground(null);
					alwaysHighlight.setForeground(null);
					autoIndent.setBackground(null);
					autoIndent.setForeground(null);
					lineBreaking.setBackground(null);
					lineBreaking.setForeground(null);
					panel.setBackground(null);
					themePanel.setBackground(null);
					themeDesc.setForeground(null);
					themeDesc.setBackground(null);
					themeButtons.setBackground(null);
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
				PreferencesHelper.getInstance().flush();
			}
		});
		settingsWindow.pack();
		settingsWindow.setResizable(false);
		settingsWindow.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		settingsWindow.setLocationRelativeTo(this);
		return settingsWindow;
	}

	private void showThemeEditor(FTheme current) {
		FThemeEditor editor = new FThemeEditor(parent, current, def);
		editor.toggleMode(dark);
		editor.setLocationRelativeTo(this);
		editor.setVisible(true);
		if (doc.isSyntaxHighlighting()) doc.updateSyntaxHighlighting();
	}

	private void showThemeEditor(FTheme current, String name) {
		FThemeEditor editor = new FThemeEditor(parent, current, name, def);
		editor.toggleMode(dark);
		editor.setLocationRelativeTo(this);
		editor.setVisible(true);
		if (doc.isSyntaxHighlighting()) doc.updateSyntaxHighlighting();
	}

	private void showSettingsWindow() {
		if (settingsWindow == null) {
			settingsWindow = createSettingsWindow();
		}
		settingsWindow.setVisible(true);
	}

	@Override
	public boolean requestFocusInWindow() {
		return pane.requestFocusInWindow();
	}

	public void setCallback(Runnable callback) {
		this.callback = callback;
	}

	private void close() {
		send(false);
		if (settingsWindow != null) {
			settingsWindow.dispose();
		}
		if (callback != null) {
			callback.run();
		}
	}

	public void send(final boolean save) {
		queue.execute(() -> {
			try {
				connection.send(Commands.Codes.SPECIAL_START + "");
				connection.send((save ? Commands.Codes.SPECIAL_SAVED : Commands.Codes.SPECIAL_CLOSED) + Commands.Codes.SPECIAL_RAW + Base64.getEncoder().encodeToString(id.getBytes(StandardCharsets.UTF_8)));
				if (save) connection.send(Commands.Codes.SPECIAL_RAW + Base64.getEncoder().encodeToString(pane.getText().getBytes(StandardCharsets.UTF_8)));
				connection.send("" + Commands.Codes.SPECIAL_END);
			} catch (IOException e) {
				System.err.println("Could not send message!");
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case Commands.Actions.ACTION_EDIT_SAVE: send(true); close(); break;

			case Commands.Actions.ACTION_SETTINGS: showSettingsWindow(); break;

			case Commands.Actions.ACTION_EDIT_CLOSE: close(); break;
		}
	}
}