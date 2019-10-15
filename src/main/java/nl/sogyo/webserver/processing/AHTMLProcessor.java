package nl.sogyo.webserver.processing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.sogyo.webserver.ContentType;
import nl.sogyo.webserver.HttpStatusCode;
import nl.sogyo.webserver.input.Request;
import nl.sogyo.webserver.output.Response;

public class AHTMLProcessor
{	
	private static final Pattern parameterRegexPattern = Pattern.compile("\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)");
	private boolean inCodeLine = false;
	private AHTMLPage page;
	
	private AHTMLProcessor(Request request)
	{
		page = new AHTMLPage();
		
		List<String[]> urlParameters = new ArrayList<>();
		for(String parName : request.getParameterNames())
			urlParameters.add(new String[] { parName, request.getParameterValue(parName) });
		page.addVariable("URL_PARS", urlParameters.toArray(String[]::new));
	}
	
	public static Response processResponse(File file, Request request)
	{
		return new AHTMLProcessor(request).processResponseInternal(file, request);
	}
	
	private Response processResponseInternal(File file, Request request)
	{
		HttpStatusCode status = HttpStatusCode.OK;
		String content = "";
		try
		{
			content = getProcessedContent(file);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			status = HttpStatusCode.ServerError;
		}
		
		return new Response(
				status,
				ContentType.AHTML,
				content.getBytes()
				);
	}
	
	private String getProcessedContent(File file) throws IOException
	{
		StringBuilder contentBuilder = new StringBuilder();
		for(String line : Files.readAllLines(file.toPath()))
		{
			String outputLine = "";
			String currentLine = line;
			int codeStartIndex = -1;
			int codeEndIndex = -1;
			while(!currentLine.isEmpty())
			{
				codeStartIndex = currentLine.indexOf("<%");
				codeEndIndex = currentLine.indexOf("%>");
				
				if(inCodeLine)
				{
					String codeLine = currentLine;
					if(codeEndIndex != -1)
					{
						codeLine = codeLine.substring(0, codeEndIndex);
						currentLine = currentLine.substring(codeEndIndex + 2);
						inCodeLine = false;
					}
					else
						currentLine = "";
					String processedLine = processLine(codeLine.strip());
					if(processedLine != null)
						outputLine += processedLine;
				}
				else
				{
					if(codeStartIndex != -1)
					{
						outputLine += currentLine.substring(0, codeStartIndex);
						currentLine = currentLine.substring(codeStartIndex + 2);
						inCodeLine = true;
					}
					else
					{
						outputLine = currentLine;
						currentLine = "";
					}
				}
			}
			
			if(!outputLine.isBlank())
				contentBuilder.append(outputLine + "\n");
		}
		
		return contentBuilder.toString();
	}
	
	private String processLine(String line)
	{
		if(line.isBlank())
			return null;
		System.out.println("Processing \"" + line + "\"");
		int nextSpaceIndex = line.indexOf(' ');
		
		String methodName = line;
		String parameterString = "";
		if(nextSpaceIndex != -1)
		{
			methodName = line.substring(0, nextSpaceIndex);
			parameterString = line.substring(nextSpaceIndex + 1);
		}
		AHTMLMethod method = AHTMLMethod.fromName(methodName);
		
		Object[] parameters = getParameters(parameterString);
		
		for(Object o : parameters)
			System.out.println("Arg: " + o);
		
		return method.execute(page, parameters);
	}
	
	private Object[] getParameters(String parameterString)
	{
		String[] splitParameterString = parameterString.split(parameterRegexPattern.pattern());
		List<Object> parameters = new ArrayList<Object>();
		
		int index = 0;
		while(index < splitParameterString.length)
		{
			Object[] result = getParameterValue(splitParameterString, index);
			parameters.add(result[0]);
			index = (int)result[1];
		}
		
		return parameters.toArray();
	}
	
	private Object[] getParameterValue(String[] parameterStrings, int parameterIndex)
	{
		Object value = null;
		
		if(parameterStrings[parameterIndex].startsWith("\""))
			value = parameterStrings[parameterIndex].substring(1, parameterStrings[parameterIndex].length() - 1).replace("\\\"", "\"");
		
		return new Object[] { value, parameterIndex + 1 };
	}
}
