package nijakow.four.client.editor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

import nijakow.four.client.Commands;
import nijakow.four.client.net.ClientConnection;

public class ClientEditor extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JTextPane pane;
	private StyledDocument doc;
	private ClientConnection connection;
	private final String id;
	private final ExecutorService queue;

	public ClientEditor(JFrame owner, ClientConnection c, ScheduledExecutorService queue, String[] args) {
		super(owner, args[1]);
		id = args[0];
		this.queue = queue;
		connection = c;
		pane = new JTextPane();
		pane.setText(args[2]);
		pane.setFont(new Font("Monospaced", Font.PLAIN, 14));
		doc = pane.getStyledDocument();
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
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				send(false);
			}
		});
		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public void send(final boolean save) {
		// TODO filter escape characters
		queue.execute(() -> {
			try {
				connection.send(Commands.SPECIAL_START + Commands.SPECIAL_EDIT + id);
				connection.send(Commands.SPECIAL_RAW + getTitle());
				if (save) {
					connection.send(Commands.SPECIAL_RAW + pane.getText());
				}
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