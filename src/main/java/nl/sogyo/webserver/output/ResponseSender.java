package nl.sogyo.webserver.output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ResponseSender
{
	public static void sendResponse(Response response, OutputStream outputStream) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
		writeStatusCode(response, writer);
		writeHeaders(response, writer);
		writer.newLine();
		writer.write(response.getContent());
		writer.flush();
	}
	
	private static void writeStatusCode(Response response, BufferedWriter writer) throws IOException
	{
		String statusString = String.format("HTTP/1.1 %s %s\r\n",
				response.getStatus().getCode(),
				response.getStatus().getDescription());
		writer.write(statusString);
	}
	
	private static void writeHeaders(Response response, BufferedWriter writer) throws IOException
	{
		writer.write(getHeaderString("date", response.getDate().format(DateTimeFormatter.RFC_1123_DATE_TIME)));
		writer.write(getHeaderString("content-length", response.getContent().length()));
		writer.write(getHeaderString("content-type", response.getContentType()));
		
		Map<String, String> customHeaders = response.getCustomHeaders();
		for(String headerKey : customHeaders.keySet())
			writer.write(getHeaderString(headerKey, customHeaders.get(headerKey)));
	}
	
	private static String getHeaderString(String key, String value)
	{
		return String.format("%s: %s\n", key, value);
	}
	
	private static String getHeaderString(String key, Object value)
	{
		return getHeaderString(key, value.toString());
	}
}
