package nl.sogyo.webserver;

public enum ContentType
{
	PlainText("text", "plain"),
	CSS("text", "css"),
	JavaScript("text", "javascript"),
	HTML("text", "html"),
	JSON("application", "json");
	
	private final String type;
	private final String subType;
	
	private ContentType(String type, String subType)
	{
		this.type = type;
		this.subType = subType;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s/%s", type, subType);
	}
}
