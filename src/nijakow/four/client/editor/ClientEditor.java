package nijakow.four.client.editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import nijakow.four.client.Commands;
import nijakow.four.client.net.ClientConnection;

public class ClientEditor extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final JTextPane pane;
	private final StyledDocument doc;
	private final ClientConnection connection;
	private final String id;
	private final String path;
	private final ScheduledExecutorService queue;
	private ScheduledFuture<?> future;
	private final Runnable highlighter = () -> updateSyntaxHighlighting();

	public ClientEditor(JFrame owner, ClientConnection c, ScheduledExecutorService queue, String[] args) {
		super(args[1]);
		path = args[1];
		id = args[0];
		this.queue = queue;
		connection = c;
		pane = new JTextPane();
		pane.setText(args[2]);
		pane.setFont(new Font("Monospaced", Font.PLAIN, 14));
		doc = pane.getStyledDocument();
		addStyles();
		getContentPane().setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(pane);
		getContentPane().add(sp, BorderLayout.CENTER);
		JButton save = new JButton("Save");
		save.addActionListener(this);
		save.setActionCommand(Commands.ACTION_EDIT_SAVE);
		JButton saveAs = new JButton("Save as");
		saveAs.addActionListener(this);
		saveAs.setActionCommand(Commands.ACTION_EDIT_SAVE_AS);
		JButton close = new JButton("Close");
		close.addActionListener(this);
		close.setActionCommand(Commands.ACTION_EDIT_CLOSE);
		JCheckBox highlight = new JCheckBox("Enable syntax highlighting");
		highlight.addItemListener(event -> {
			if (highlight.isSelected()) {
				startSyntaxHighlighting();
			} else {
				stopSyntaxHighlighting();
			}
		});
		if (path.endsWith(".c")) {
			highlight.setSelected(true);
		}
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 3));
		buttons.add(close);
		buttons.add(saveAs);
		buttons.add(save);
		JPanel allButtons = new JPanel();
		allButtons.setLayout(new GridLayout(2, 1));
		allButtons.add(buttons);
		allButtons.add(highlight);
		getContentPane().add(allButtons, BorderLayout.SOUTH);
		setPreferredSize(new Dimension(300, 200));
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(owner);
	}

	private void startSyntaxHighlighting() {
		future = queue.scheduleWithFixedDelay(highlighter, 0, 500, TimeUnit.MILLISECONDS);
	}

	private  void stopSyntaxHighlighting() {
		future.cancel(false);
	}

	public void dispose() {
		send(false, null);
		super.dispose();
	}
	
	private void addStyles() {
		final Style def = pane.getLogicalStyle();
		Style s = doc.addStyle(Commands.STYLE_KEYWORD, def);
		StyleConstants.setForeground(s, Color.red);
		StyleConstants.setBold(s, true);
		s = doc.addStyle(Commands.STYLE_TYPE, def);
		StyleConstants.setForeground(s, Color.green);
		s = doc.addStyle(Commands.STYLE_SPECIAL_WORD, def);
		StyleConstants.setItalic(s, true);
		StyleConstants.setForeground(s, Color.blue);
		s = doc.addStyle(Commands.STYLE_STDLIB, def);
		StyleConstants.setForeground(s, Color.orange);
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
					doc.getStyle(Commands.STYLE_KEYWORD), true);
		keywords = "\\b(any|void|int|char|bool|string|object|list|mapping)\\b";
		matcher = Pattern.compile(keywords).matcher(pane.getText());
		while (matcher.find())
			doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
					doc.getStyle(Commands.STYLE_TYPE), true);
		keywords = "\\b(true|false|nil|va_next|va_count)\\b";
		matcher = Pattern.compile(keywords).matcher(pane.getText());
		while (matcher.find())
			doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
					doc.getStyle(Commands.STYLE_SPECIAL_WORD), true);
		keywords = "\\b(create|the|call|log|length|insert|append|remove|strlen|chr|write|prompt|password|edit)\\b";
		matcher = Pattern.compile(keywords).matcher(pane.getText());
		while (matcher.find())
			doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(),
					doc.getStyle(Commands.STYLE_STDLIB), true);
	}

	public void send(final boolean save, final String newPath) {
		// TODO filter escape characters
		queue.execute(() -> {
			try {
				connection.send(Commands.SPECIAL_START + Commands.SPECIAL_EDIT + id);
				if (newPath != null)
					connection.send(Commands.SPECIAL_RAW + newPath);
				if (save)
					connection.send(Commands.SPECIAL_RAW + pane.getText());
				connection.send("" + Commands.SPECIAL_END);
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
			case Commands.ACTION_EDIT_SAVE:
				send(true, null);
				break;

			case Commands.ACTION_EDIT_SAVE_AS:
				saveAs();
				break;

			case Commands.ACTION_EDIT_CLOSE:
				send(false, null);
				dispose();
				break;
		}
	}
}