package nl.sogyo.webserver.processing;

public enum AHTMLValueType
{
	Integer("INT", Integer.class, 0),
	Double("FLT", Double.class, 0.0),
	String("STR", String.class, ""),
	Array("ARR", Object[].class, null),
	;
	
	private final String typeName;
	private final Class innerType;
	private final Object defaultValue;
	
	private AHTMLValueType(String typeName, Class innerType, Object defaultValue)
	{
		this.typeName = typeName;
		this.innerType = innerType;
		this.defaultValue = defaultValue;
	}
	
	public boolean isArray()
	{
		return innerType.isArray();
	}
	
	public Object getDefaultValue()
	{
		return defaultValue;
	}
	
	public static AHTMLValueType fromName(String typeName)
	{
		for(AHTMLValueType valueType : AHTMLValueType.values())
		{
			if(valueType.typeName.equals(typeName))
				return valueType;
		}
		
		return null;
	}
}
