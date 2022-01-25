package nijakow.four.client.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
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
	private final ExecutorService queue;

	public ClientEditor(JFrame owner, ClientConnection c, ScheduledExecutorService queue, String[] args) {
		super(args[1]);
		id = args[0];
		this.queue = queue;
		connection = c;
		pane = new JTextPane();
		pane.setText(args[2]);
		pane.setFont(new Font("Monospaced", Font.PLAIN, 14));
		doc = pane.getStyledDocument();
		addStyles();
		pane.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				updateSyntaxHighlighting();
			}
		});
		getContentPane().setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(pane);
		getContentPane().add(sp, BorderLayout.CENTER);
		JButton accept = new JButton("Accept Changes");
		accept.addActionListener(this);
		accept.setActionCommand(Commands.ACTION_EDIT_ACCEPT);
		JButton reject = new JButton("Reject Changes");
		reject.addActionListener(this);
		reject.setActionCommand(Commands.ACTION_EDIT_REJECT);
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 2));
		buttons.add(reject);
		buttons.add(accept);
		getContentPane().add(buttons, BorderLayout.SOUTH);
		pack();
		updateSyntaxHighlighting();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(owner);
	}

	public void dispose() {
		send(false);
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
		queue.execute(() -> {
			Style defaultStyle = StyleContext.
					   getDefaultStyleContext().
					   getStyle(StyleContext.DEFAULT_STYLE);
			doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);
			String keywords = "\\b(new|struct|class|inherits|use|this|if|else|while|for|break|continue|switch|case|return)\\b";
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
		});
	}
	
	public void send(final boolean save) {
		// TODO filter escape characters
		queue.execute(() -> {
			try {
				connection.send(Commands.SPECIAL_START + Commands.SPECIAL_EDIT + id);
				if (save)
					connection.send(Commands.SPECIAL_RAW + pane.getText());
				connection.send("" + Commands.SPECIAL_END);
			} catch (IOException e) {
				System.err.println("Could not send message!");
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case Commands.ACTION_EDIT_ACCEPT:
			send(true);
			dispose();
			break;

		case Commands.ACTION_EDIT_REJECT:
			send(false);
			dispose();
			break;
		}
	}
}