package nl.sogyo.webserver.output;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import nl.sogyo.webserver.ContentType;
import nl.sogyo.webserver.HttpStatusCode;
import nl.sogyo.webserver.ServerProperties;
import nl.sogyo.webserver.input.Request;
import nl.sogyo.webserver.processing.AHTMLProcessor;

public class ResponseFetcher
{
	private static final String[] indexExtensions = {".html", ".php", ".shtml", ".ahtml"};
	
	public static Response fetch(Request request, ServerProperties properties)
	{
		String resourcePath = properties.rootFolder + request.getResourcePath();
		Response response = fetch(resourcePath, request);
		if(response == null)
			return getErrorResponse(HttpStatusCode.NotFound);
		else
			return response;
	}
	
	private static Response fetch(String resourcePath, Request request)
	{
		URL resourceURL = ResponseFetcher.class.getClassLoader().getResource(resourcePath);
		if(resourceURL == null)
			return null;
		String localResourcePath = resourceURL.getFile().replace("%20", " ");
		
		File file = new File(localResourcePath);
		
		if(file.isDirectory())
		{
			int index = 0;
			do
			{
				file = new File(localResourcePath + "index" + indexExtensions[index]);
				index++;
			}
			while(index < indexExtensions.length && !file.exists());
		}
		
		if(file.exists())
		{
			Response response = null;
			try
			{
				ContentType contentType = determineContentType(file);
				
				if(contentType == ContentType.AHTML)
					response = AHTMLProcessor.processResponse(file, request);
				else
				{
					response = new Response(
						HttpStatusCode.OK,
						contentType,
						getFileContents(file.toPath())
						);
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
				response = getErrorResponse(HttpStatusCode.ServerError);
			}
			return response;
		}
		else
			return null;
	}
	
	private static Response getErrorResponse(HttpStatusCode statusCode)
	{
		Response templateResponse = fetch(statusCode.getCode() + ".shtml", null);
		if(templateResponse == null)
			return new Response(statusCode, ContentType.PlainText, String.format("Error %d: %s", statusCode.getCode(), statusCode.getDescription()).getBytes());
		else
		{
			if(templateResponse.getStatus() == HttpStatusCode.OK)
				return new Response(statusCode, templateResponse.getContentType(), templateResponse.getContent());
			else
				return templateResponse; //Because this is probably a server error response!
		}
	}
	
	private static ContentType determineContentType(File file)
	{
		String extension = getFileExtension(file).toLowerCase();
		
		switch(extension)
		{
		case "html":
		case "shtml":
		case "php":
			return ContentType.HTML;
		case "ahtml":
			return ContentType.AHTML;
		case "css":
			return ContentType.CSS;
		case "json":
			return ContentType.JSON;
		case "js":
			return ContentType.JavaScript;
		case "gif":
			return ContentType.GIF;
		case "jpg":
		case "jpeg":
			return ContentType.JPEG;
		case "png":
			return ContentType.PNG;
		default:
			return ContentType.PlainText;
		}
	}
	
	private static String getFileExtension(File file)
	{
		String fileName = file.getName();
		int extensionStart = fileName.lastIndexOf('.') + 1;
		if(extensionStart > 1)
			return fileName.substring(extensionStart);
		else
			return "";
	}
	
	private static byte[] getFileContents(Path path) throws IOException
	{
		byte[] contents = Files.readAllBytes(path);
		return contents;
	}
}
