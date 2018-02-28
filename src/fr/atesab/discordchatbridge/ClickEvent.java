package fr.atesab.discordchatbridge;

public class ClickEvent {
    public enum Action
    {
        OPEN_URL,
        OPEN_FILE,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        CHANGE_PAGE
    }
    public static final ClickEvent EMPTY = new ClickEvent();
	private Action action;
	private String value;
	private ClickEvent() {}

	public ClickEvent(Action action, String value) {
		this.action = action;
		this.value = value;
	}

	public Action getAction() {
		return action;
	}

	public String getRaw() {
		return "{\"action\":\""+action.name().toLowerCase()+"\", \"value\":\""+value.replaceAll("\"", "\\u0022")+"\"}";
	}

	public String getValue() {
		return value;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
