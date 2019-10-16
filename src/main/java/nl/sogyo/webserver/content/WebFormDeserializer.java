package nl.sogyo.webserver.content;

import java.util.HashMap;
import java.util.Map;

public class WebFormDeserializer
{
	public static Map<String, Object> deserialize(String raw)
	{
		Map<String, Object> formData = new HashMap<>();
		
		String[] kvPairs = raw.split("&");
		
		for(String kvPairConcat : kvPairs)
		{
			String[] kvPair = kvPairConcat.split("=");
			if(kvPair.length == 2)
				formData.put(kvPair[0].replace("+", " "), kvPair[1].replace("+", " "));
		}
		
		return formData;
	}
}
