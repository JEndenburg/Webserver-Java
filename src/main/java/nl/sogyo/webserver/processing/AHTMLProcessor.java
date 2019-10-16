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
		page.addVariable("URL_PARS", urlParameters.toArray(Object[]::new));
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
		int indexShift = 1;
		
		if(parameterIndex >= parameterStrings.length)
			return new Object[] { null, parameterIndex + indexShift };
		
		if(parameterStrings[parameterIndex].startsWith("\"") && parameterStrings[parameterIndex].endsWith("\""))
			value = parameterStrings[parameterIndex].substring(1, parameterStrings[parameterIndex].length() - 1).replace("\\\"", "\"");
		else if(isIntegerNumber(parameterStrings[parameterIndex]))
			value = Integer.parseInt(parameterStrings[parameterIndex]);
		else if(isDoubleNumber(parameterStrings[parameterIndex]))
			value = Double.parseDouble(parameterStrings[parameterIndex]);
		else if(parameterStrings[parameterIndex].startsWith("$"))
		{
			Object[] result = getVariableValue(parameterStrings[parameterIndex].substring(1), parameterStrings, parameterIndex);
			value = result[0];
			indexShift += (int)result[1];
		}
		
		return new Object[] { value, parameterIndex + indexShift };
	}
	
	private boolean isIntegerNumber(String string)
	{
		try
		{
			Integer.parseInt(string);
			return true;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
	}
	
	private boolean isDoubleNumber(String string)
	{
		try
		{
			Double.parseDouble(string);
			return true;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
	}
	
	private Object[] getVariableValue(Object value, String[] parameterStrings, int currentIndex)
	{
		int indexShift = 0;
		
		if(value.getClass().isArray())
		{
			int nextIndex = currentIndex + 1;
			if(nextIndex < parameterStrings.length && parameterStrings[nextIndex].startsWith("@"))
			{
				parameterStrings[nextIndex] = parameterStrings[nextIndex].substring(1);
				Object[] nextValue = getParameterValue(parameterStrings, nextIndex);
				int selectedIndex = (int)nextValue[0];
				Object[] arrVal = (Object[])value;
				if(selectedIndex < arrVal.length)
					value = ((Object[]) value)[selectedIndex];
				else
					value = null;
				indexShift = (int)nextValue[1];
				
				if(value != null && !value.getClass().isPrimitive())
				{
					Object[] subValue = getVariableValue(value, parameterStrings, currentIndex + 1);
					value = subValue[0];
					indexShift += (int)subValue[1];
				}
			}
		}
		
		return new Object[] { value, indexShift };
	}
	
	private Object[] getVariableValue(String variableName, String[] parameterStrings, int currentIndex)
	{
		return getVariableValue(page.getVariable(variableName), parameterStrings, currentIndex);
	}
}
