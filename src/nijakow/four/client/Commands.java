package nijakow.four.client;

import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;

public abstract class Commands {
	public static final String UPLOAD = "upload";

	public abstract static class Actions {
		public static final String ACTION_SEND               = "send";
		public static final String ACTION_SETTINGS           = "settings";
		public static final String ACTION_PASSWORD           = "password";
		public static final String ACTION_RECONNECT          = "reconnect";
		public static final String ACTION_EDIT_SAVE          = "editor/accept";
		public static final String ACTION_EDIT_SAVE_AS       = "editor/saveAs";
		public static final String ACTION_EDIT_CLOSE         = "editor/reject";
		public static final String ACTION_STATUS_LABEL_TIMER = "invisible";
	}

	public abstract static class Codes {
		public static final String SPECIAL_CLOSED = "editor/cancelled";
		public static final String SPECIAL_EDIT   = "editor/edit";
		public static final String SPECIAL_IMG    = "media/image";
		public static final String SPECIAL_RAW    = ":";
		public static final String SPECIAL_SAVED  = "editor/saved";
		public static final String SPECIAL_PROMPT = "prompt/plain";
		public static final String SPECIAL_PWD    = "prompt/password";
		public static final String SPECIAL_UPLOAD = "file/upload";
		public static final char SPECIAL_START    = 0x02;
		public static final char SPECIAL_END      = 0x03;
	}

	public abstract static class Keys {
		public static final KeyStroke LEFT  = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
		public static final KeyStroke RIGHT = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
		public static final KeyStroke DOWN  = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
		public static final KeyStroke UP    = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
		public static final KeyStroke ENTER = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
	}

	public abstract static class Styles {
		public static final String STYLE_BOLD        = "BOLD";
		public static final String STYLE_ERROR       = "error";
		public static final String STYLE_INPUT       = "input";
		public static final String STYLE_NORMAL      = "RESET";
		public static final String STYLE_BG_RGB      = "BG_0x";
		public static final String STYLE_FG_RGB      = "FG_0x";
		public static final String STYLE_ITALIC      = "ITALIC";
		public static final String STYLE_UNDERSCORED = "UNDERSCORED";
		@Deprecated public static final String STYLE_BG_RED    = "BG_RED";
		@Deprecated	public static final String STYLE_BG_BLUE   = "BG_BLUE";
		@Deprecated	public static final String STYLE_BG_BLACK  = "BG_BLACK";
		@Deprecated	public static final String STYLE_BG_GREEN  = "BG_GREEN";
		@Deprecated	public static final String STYLE_BG_YELLOW = "BG_YELLOW";
		@Deprecated public static final String STYLE_RED       = "RED";
		@Deprecated	public static final String STYLE_BLUE      = "BLUE";
		@Deprecated public static final String STYLE_BLACK     = "BLACK";
		@Deprecated	public static final String STYLE_GREEN     = "GREEN";
		@Deprecated	public static final String STYLE_YELLOW    = "YELLOW";
	}
}