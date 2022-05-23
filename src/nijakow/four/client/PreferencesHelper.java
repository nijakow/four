package nijakow.four.client;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import nijakow.four.server.Four;

public class PreferencesHelper {
	private static PreferencesHelper instance;
	private final Preferences prefs;
	
	private PreferencesHelper() {
		prefs = Preferences.userNodeForPackage(Four.class);
	}

	public int getWindowPositionX() {
		return prefs.getInt(Key.WINDOW_POS_X, -1);
	}
	
	public int getWindowPositionY() {
		return prefs.getInt(Key.WINDOW_POS_Y, -1);
	}
	
	public int getWindowHeight() {
		return prefs.getInt(Key.WINDOW_HEIGHT, -1);
	}
	
	public int getWindowWidth() {
		return prefs.getInt(Key.WINDOW_WIDTH, -1);
	}

	public int getEditorPositionX() {
		return prefs.getInt(Key.EDITOR_POS_X, -1);
	}

	public int getEditorPositionY() {
		return prefs.getInt(Key.EDITOR_POS_Y, -1);
	}

	public int getEditorHeight() {
		return prefs.getInt(Key.EDITOR_HEIGHT, -1);
	}

	public int getEditorWidth() {
		return prefs.getInt(Key.EDITOR_WIDTH, -1);
	}

	public int getPort() {
		return prefs.getInt(Key.PORT_NUMBER, 4242);
	}
	
	public String getHostname() {
		return prefs.get(Key.HOSTNAME, "");
	}
	
	public boolean getLineBreaking() {
		return prefs.getBoolean(Key.LINE_BREAKING, true);
	}

	public boolean getDarkMode() {
		return prefs.getBoolean(Key.DARK_MODE, false);
	}

	public boolean getEditorAlwaysHighlight() {
		return prefs.getBoolean(Key.EDITOR_HIGHLIGHT, false);
	}

	public boolean getEditorLineBreaking() {
		return prefs.getBoolean(Key.EDITOR_LINE_BREAK, true);
	}

	public String getUIManagerName() {
		return prefs.get(Key.UI_MANAGER_NAME, null);
	}

	public boolean getAutoIndenting() {
		return prefs.getBoolean(Key.AUTO_INDENTING, true);
	}

	public boolean getShiftForNewline() { return prefs.getBoolean(Key.SHIFT_FOR_NEWLINE, false); }

	public String getEditorTheme() {
		return prefs.get(Key.EDITOR_THEME, Commands.Themes.DEFAULT);
	}

	public void setEditorTheme(String theme) {
		prefs.put(Key.EDITOR_THEME, theme);
	}

	public void setAutoIndenting(boolean autoIndenting) {
		prefs.putBoolean(Key.AUTO_INDENTING, autoIndenting);
	}

	public void setUIManagerName(String name) {
		prefs.put(Key.UI_MANAGER_NAME, name);
	}

	public void setEditorLineBreaking(boolean breaking) {
		prefs.putBoolean(Key.EDITOR_LINE_BREAK, breaking);
	}

	public void setEditorAlwaysHighlight(boolean highlight) {
		prefs.putBoolean(Key.EDITOR_HIGHLIGHT, highlight);
	}

	public void setLineBreaking(boolean breaking) {
		prefs.putBoolean(Key.LINE_BREAKING, breaking);
	}
	
	public void setWindowDimensions(int posX, int posY, int width, int height) {
		setWindowPosition(posX, posY);
		setWindowSize(width, height);
	}
	
	public void setWindowSize(int width, int height) {
		prefs.putInt(Key.WINDOW_HEIGHT, height);
		prefs.putInt(Key.WINDOW_WIDTH, width);
	}
	
	public void setWindowPosition(int x, int y) {
		prefs.putInt(Key.WINDOW_POS_X, x);
		prefs.putInt(Key.WINDOW_POS_Y, y);
	}

	public void setEditorSize(int width, int height) {
		prefs.putInt(Key.EDITOR_WIDTH, width);
		prefs.putInt(Key.EDITOR_HEIGHT, height);
	}

	public void setEditorPosition(int x, int y) {
		prefs.putInt(Key.EDITOR_POS_X, x);
		prefs.putInt(Key.EDITOR_POS_Y, y);
	}

	public void setEditorDimensions(int posX, int posY, int width, int height) {
		setEditorPosition(posX, posY);
		setEditorSize(width, height);
	}

	public void setHostname(String hostname) {
		prefs.put(Key.HOSTNAME, hostname);
	}
	
	public void setPort(int port) {
		prefs.putInt(Key.PORT_NUMBER, port);
	}

	public void setDarkMode(boolean enabled) {
		prefs.putBoolean(Key.DARK_MODE, enabled);
	}

	public void setShiftForNewline(boolean value) { prefs.putBoolean(Key.SHIFT_FOR_NEWLINE, value); }

	public void flushOrThrow() throws BackingStoreException {
		prefs.flush();
	}

	public boolean flush() {
		try {
			flushOrThrow();
		} catch (BackingStoreException e) {
			System.err.println("Could not save settings: " + e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static PreferencesHelper getInstance() {
		if (instance == null) {
			instance = new PreferencesHelper();
		}
		return instance;
	}

	public abstract static class Key {
		public static final String AUTO_INDENTING    = "autoIndent";
		public static final String DARK_MODE         = "darkMode";
		public static final String EDITOR_HEIGHT     = "editorHeight";
		public static final String EDITOR_HIGHLIGHT  = "editorHighlighting";
		public static final String EDITOR_LINE_BREAK = "editorLineBreaking";
		public static final String EDITOR_THEME      = "editorTheme";
		public static final String EDITOR_POS_X      = "editorPosX";
		public static final String EDITOR_POS_Y      = "editorPosY";
		public static final String EDITOR_WIDTH      = "editorWidth";
		public static final String HOSTNAME          = "hostname";
		public static final String LINE_BREAKING     = "lineBreaking";
		public static final String SHIFT_FOR_NEWLINE = "shiftForNewline";
		public static final String PORT_NUMBER       = "portNumber";
		public static final String WINDOW_POS_X      = "windowPosX";
		public static final String WINDOW_POS_Y      = "windowPosY";
		public static final String WINDOW_WIDTH      = "windowWidth";
		public static final String WINDOW_HEIGHT     = "windowHeight";
		public static final String UI_MANAGER_NAME   = "uiName";
	}
}