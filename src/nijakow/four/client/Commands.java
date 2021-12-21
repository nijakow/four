package nijakow.four.client;

public abstract class Commands {
	public static final String ACTION_SETTINGS = "settings";
	public static final String ACTION_SEND = "send";
	public static final String ACTION_STATUS_LABEL_TIMER = "invisible";
	public static final String ACTION_PASSWORD = "password";
	public static final String ACTION_EDIT_REJECT = "editor/reject";
	public static final String ACTION_EDIT_ACCEPT = "editor/accept";
	public static final String STYLE_ERROR = "error";
	public static final String STYLE_INPUT = "input";
	public static final String STYLE_NORMAL = "RESET";
	public static final String STYLE_BG_BLUE = "BG_BLUE";
	public static final String STYLE_BLUE = "BLUE";
	public static final String STYLE_BG_GREEN = "BG_GREEN";
	public static final String STYLE_GREEN = "GREEN";
	public static final String STYLE_BG_YELLOW = "BG_YELLOW";
	public static final String STYLE_YELLOW = "YELLOW";
	public static final String STYLE_BG_BLACK = "BG_BLACK";
	public static final String STYLE_BLACK = "BLACK";
	public static final String STYLE_BG_RED = "BG_RED";
	public static final String STYLE_RED = "RED";
	public static final String STYLE_ITALIC = "ITALIC";
	public static final String STYLE_BOLD = "BOLD";
	public static final String STYLE_UNDERSCORED = "UNDERSCORED";
	public static final String SPECIAL_EDIT = "$";
	public static final String SPECIAL_RAW = ":";
	public static final String SPECIAL_PWD = "?";
	public static final String SPECIAL_PROMPT = ".";
	public static final char SPECIAL_START = 0x02;
	public static final char SPECIAL_END = 0x03;
}