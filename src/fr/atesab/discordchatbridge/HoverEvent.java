package fr.atesab.discordchatbridge;

public class HoverEvent {
    public enum Action
    {
        SHOW_TEXT,
        SHOW_ACHIEVEMENT,
        SHOW_ITEM,
        SHOW_ENTITY
    }
    public static final HoverEvent EMPTY = new HoverEvent();

	private Action action;
	private String value;
    private HoverEvent() {}
	public HoverEvent(Action action, String value) {
		this.action = action;
		this.value = value;
	}
	public String getRaw() {
		return "{\"action\":\""+action.name().toLowerCase()+"\", \"value\":"+value+"}";
	}
}
