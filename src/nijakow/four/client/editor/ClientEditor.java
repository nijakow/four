package nijakow.four.client.editor;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.*;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.*;

import nijakow.four.client.Commands;
import nijakow.four.client.PreferencesHelper;
import nijakow.four.client.net.ClientConnection;
import nijakow.four.client.utils.StringHelper;

public class ClientEditor extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final JCheckBox highlight;
	private final JScrollPane scrollPane;
	private final JTextPane pane;
	private final FSuggestionMenu popup; // TODO Write subclass! - mhahnFr
	private final FDocument doc;
	private final ClientConnection connection;
	private final String id;
	private final String path;
	private final ScheduledExecutorService queue;
	private JDialog settingsWindow;
	private boolean dark;

	public ClientEditor(ClientConnection c, String id, String path, String content) {
		super(path);
		this.path = path;
		this.id = id;
		queue = Executors.newSingleThreadScheduledExecutor();
		connection = c;
		pane = new JTextPane();
		doc = new FDocument();
		pane.setDocument(doc);
		pane.setText(content);
		pane.setFont(new Font("Monospaced", Font.PLAIN, 14));
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
					if (popup.isVisible()) popup.setVisible(false);
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
					if (popup.isVisible()) popup.setVisible(false);
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		});
		m.addActionForKeyStroke(Commands.Keys.ENTER, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tabs;
				synchronized (doc) {
					tabs = StringHelper.generateFilledString(' ', doc.getIndentationLevel(pane.getCaretPosition()) * 4);
				}
				try {
					doc.insertString(pane.getCaretPosition(), "\n" + tabs, null);
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
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

	private  void stopSyntaxHighlighting() {
		doc.setHighlightingEnabled(false);
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
			pane.setBackground(Color.white);
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
		if (settingsWindow != null) {
			settingsWindow.dispose();
		}
		super.dispose();
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