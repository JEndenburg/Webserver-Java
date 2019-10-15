package nl.sogyo.webserver.processing;

import java.util.HashMap;
import java.util.Map;

public class AHTMLPage
{
	private Map<String, Object> variableMap = new HashMap<>();
	
	public void addVariable(String variableName, Object value)
	{
		if(variableMap.containsKey(variableName))
			variableMap.replace(variableName, value);
		else
			variableMap.put(variableName, value);
	}
	
	public Object getVariable(String variableName)
	{
		if(variableMap.containsKey(variableName))
			return variableMap.get(variableName);
		else
			return null;
	}
}
