package fr.atesab.discordchatbridge;

import org.bukkit.ChatColor;

public class ChatComponent {
    private ChatColor color;
    private String text;
	private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;

    public ChatComponent(String text) {
		super();
		this.text = text;
	}
	public Boolean getBold() {
		return bold;
	}
	public ClickEvent getClickEvent() {
		return clickEvent;
	}
	public ChatColor getColor() {
		return color;
	}
	public HoverEvent getHoverEvent() {
		return hoverEvent;
	}
	public Boolean getItalic() {
		return italic;
	}
	public Boolean getObfuscated() {
		return obfuscated;
	}
	public String getRaw() {
		String s="";
		if(getBold()!=null)s+=", \"bold\":\""+getBold()+"\"";
		if(getClickEvent()!=null) s+=", \"clickEvent\":"+getClickEvent().getRaw();
		if(getHoverEvent()!=null) s+=", \"hoverEvent\":"+getHoverEvent().getRaw();
		if(getColor()!=null)s+=", \"color\":\""+getColor().name().toLowerCase()+"\"";
		if(getItalic()!=null)s+=", \"italic\":\""+getItalic()+"\"";
		if(getUnderlined()!=null)s+=", \"underlined\":\""+getUnderlined()+"\"";
		if(getStrikethrough()!=null)s+=", \"strikethrough\":\""+getStrikethrough()+"\"";
		if(getObfuscated()!=null)s+=", \"obfuscated\":\""+getObfuscated()+"\"";
		if(s.isEmpty())return "\""+getText().replaceAll("\"", "\\\\\"")+"\"";
		else return "{\"text\":\""+getText().replaceAll("\"", "\\\\\"")+"\""+s+"}";
	}
	public Boolean getStrikethrough() {
		return strikethrough;
	}
	public String getText() {
		return text;
	}
	public Boolean getUnderlined() {
		return underlined;
	}
	public void setBold(Boolean bold) {
		this.bold = bold;
	}
	public void setClickEvent(ClickEvent clickEvent) {
		this.clickEvent = clickEvent;
	}
	public void setColor(ChatColor color) {
		this.color = color;
	}
	public void setHoverEvent(HoverEvent hoverEvent) {
		this.hoverEvent = hoverEvent;
	}
	public void setItalic(Boolean italic) {
		this.italic = italic;
	}
	public void setObfuscated(Boolean obfuscated) {
		this.obfuscated = obfuscated;
	}
	public void setStrikethrough(Boolean strikethrough) {
		this.strikethrough = strikethrough;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setUnderlined(Boolean underlined) {
		this.underlined = underlined;
	}
}
