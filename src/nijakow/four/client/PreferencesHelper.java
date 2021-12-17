package nijakow.four.client;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import nijakow.four.Four;

public class PreferencesHelper {
	public static final String WINDOW_POS_X = "windowPosX";
	public static final String WINDOW_POS_Y = "windowPosY";
	public static final String WINDOW_WIDTH = "windowWidth";
	public static final String WINDOW_HEIGHT = "windowHeight";
	private Preferences prefs;
	
	public PreferencesHelper() {
		prefs = Preferences.userNodeForPackage(Four.class);
	}
	
	public int getWindowPositionX() {
		return prefs.getInt(WINDOW_POS_X, -1);
	}
	
	public int getWindowPositionY() {
		return prefs.getInt(WINDOW_POS_Y, -1);
	}
	
	public int getWindowHeight() {
		return prefs.getInt(WINDOW_HEIGHT, -1);
	}
	
	public int getWindowWidth() {
		return prefs.getInt(WINDOW_WIDTH, -1);
	}
	
	public void setWindowDimensions(int posX, int posY, int width, int height) {
		setWindowPosition(posX, posY);
		setWindowSize(width, height);
	}
	
	public void setWindowSize(int width, int height) {
		prefs.putInt(WINDOW_HEIGHT, height);
		prefs.putInt(WINDOW_WIDTH, width);
	}
	
	public void setWindowPosition(int x, int y) {
		prefs.putInt(WINDOW_POS_X, x);
		prefs.putInt(WINDOW_POS_Y, y);
	}
	
	public boolean flush() {
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			System.err.println("Could not save settings: " + e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
}