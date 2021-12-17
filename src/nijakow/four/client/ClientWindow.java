package nijakow.four.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClientWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String SETTINGS = "settings";
	private static final String SEND = "send";
	private JTextField prompt;
	
	public ClientWindow() {
		super("Nijakow's \"Four\"");
		getContentPane().setLayout(new BorderLayout());
		JPanel south = new JPanel();
		south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
		prompt = new JTextField();
		prompt.addActionListener(this);
		prompt.setActionCommand(SEND);
		JButton settings = new JButton("Settings");
		settings.addActionListener(this);
		settings.setActionCommand(SETTINGS);
		south.add(settings);
		south.add(prompt);
		getContentPane().add(south, BorderLayout.SOUTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// TODO save constraints
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case SETTINGS:
			System.out.println("TODO: settings");
			break;
			
		case SEND:
			System.out.println("TODO: send \"" + prompt.getText() + "\"");
			prompt.setText("");
			break;
		}
	}
	
	public static void openWindow() {
		EventQueue.invokeLater(() -> new ClientWindow());
	}
}