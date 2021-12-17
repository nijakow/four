package nijakow.four.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
	private PreferencesHelper prefs;
	
	public ClientWindow() {
		super("Nijakow's \"Four\"");
		// TODO macOS customization
		prefs = new PreferencesHelper();
		getContentPane().setLayout(new BorderLayout());
		JPanel south = new JPanel();
		south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
		prompt = new JTextField();
		prompt.addActionListener(this);
		prompt.setActionCommand(SEND);
		JButton settings = new JButton("Settings");
		settings.addActionListener(this);
		settings.setActionCommand(SETTINGS);
		south.add(prompt);
		south.add(settings);
		getContentPane().add(south, BorderLayout.SOUTH);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				prefs.setWindowDimensions(getX(), getY(), getWidth(), getHeight());
				prefs.flush();
				System.out.println("TODO: Close connection");
				dispose();
			}
		});
		int x = prefs.getWindowPositionX();
		int y = prefs.getWindowPositionY();
		if (x == -1 || y == -1)
			setLocationRelativeTo(null);
		else
			setLocation(x, y);
		int width = prefs.getWindowWidth();
		int height = prefs.getWindowHeight();
		if (width == -1 || height == -1)
			pack();
		else
			setSize(width, height);
	}
	
	private void openSettingsWindow() {
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case SETTINGS:
			openSettingsWindow();
			System.out.println("TODO: settings");
			break;
			
		case SEND:
			System.out.println("TODO: send \"" + prompt.getText() + "\"");
			prompt.setText("");
			break;
		}
	}
	
	public static void openWindow() {
		EventQueue.invokeLater(() -> new ClientWindow().setVisible(true));
		// TODO connection stuff
	}
}