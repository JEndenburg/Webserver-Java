package nl.sogyo.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerProperties
{
	public Integer port;
	public String rootFolder;
	
	private ServerProperties() {}
	
	public static ServerProperties loadProperties(String path)
	{
		FileInputStream fileInput = null;
		FileOutputStream fileOutput = null;
		Properties properties = new Properties();
		
		ServerProperties serverProperties = new ServerProperties();
		
		try
		{
			if(new File(path).exists())
			{
				fileInput = new FileInputStream(path);
				properties.load(fileInput);
			}
			fileOutput = new FileOutputStream(path);
			serverProperties.loadProperties(properties);
			serverProperties.saveProperties(properties);
			properties.store(fileOutput, "Server Config Properties");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(fileInput != null)
					fileInput.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return serverProperties;
	}
	
	private void loadProperties(Properties prop)
	{
		port = Integer.parseInt(loadProperty(prop, "Port", 9090));
		rootFolder = loadProperty(prop, "Root", "/public_html/");
	}
	
	private void saveProperties(Properties prop)
	{
		prop.setProperty("Port", port.toString());
		prop.setProperty("Root", rootFolder);
	}
	
	private static String loadProperty(Properties property, String key, Object defaultValue)
	{
		if(property.containsKey(key))
			return (String)property.get(key);
		else
			return (String)defaultValue;
	}
}
