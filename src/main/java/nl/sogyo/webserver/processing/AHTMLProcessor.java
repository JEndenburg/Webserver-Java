package nl.sogyo.webserver.processing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.sogyo.webserver.ContentType;
import nl.sogyo.webserver.HttpStatusCode;
import nl.sogyo.webserver.input.Request;
import nl.sogyo.webserver.output.Response;
import nl.sogyo.webserver.processing.AHTMLScript.MethodCall;

public class AHTMLProcessor
{	
	private static final Pattern parameterRegexPattern = Pattern.compile("\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)");
	private boolean inCodeLine = false;
	private AHTMLScript script;
	
	private AHTMLProcessor(Request request)
	{
		script = new AHTMLScript();
		
		List<String[]> urlParameters = new ArrayList<>();
		for(String parName : request.getParameterNames())
			urlParameters.add(new String[] { parName, request.getParameterValue(parName) });
		script.addVariable("URL_PARS", urlParameters.toArray(Object[]::new));
		script.addVariable("METHOD", request.getHTTPMethod().toString());
		
		Map<String, Object> bodyMap = request.getBody();
		
		if(bodyMap != null)
		{
			List<String[]> body = new ArrayList<>();
			for(String bodyKey : bodyMap.keySet())
				script.addVariable("BODY:" + bodyKey.strip().replace(' ', '_'), bodyMap.get(bodyKey));
		}
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
		String outputLine = "";
		for(String line : Files.readAllLines(file.toPath()))
		{
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
			
			if(!inCodeLine)
			{
				if(!outputLine.isBlank())
					contentBuilder.append(outputLine + "\n");
				outputLine = "";
			}
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
		
		if(method == null)
		{
			System.out.println("Unknown method \"" + methodName + "\" was called.");
			return null;
		}
		
		Object[] parameters;
		
		String[] splitParameterString = parameterString.split(parameterRegexPattern.pattern());
		switch(method.getParameterRequestType())
		{
		default:
		case Regular:
			parameters = getParameters(parameterString, splitParameterString);
			break;
		case TypeAndVariableName:
			parameters = getTypeAndVariableNameParameters(parameterString, splitParameterString);
			break;
		case VariableNameAndValues:
			parameters = getVariableNameAndValuesParameters(parameterString, splitParameterString);
			break;
		}
		
		MethodCall methodCall = new MethodCall(method, parameters, splitParameterString);
		return script.executeMethod(methodCall);
	}
	
	private Object[] getVariableNameAndValuesParameters(String parameterString, String[] splitParameterString)
	{
		if(splitParameterString[0].startsWith("$"))
		{
			String passingParameterString = parameterString.substring(splitParameterString[0].length() + 1);
			String[] passingSplitParameterString = passingParameterString.split(parameterRegexPattern.pattern());
			Object[] valuedParameters = getParameters(passingParameterString, passingSplitParameterString);
			
			Object[] returnValues = new Object[valuedParameters.length + 1];
			System.arraycopy(valuedParameters, 0, returnValues, 1, valuedParameters.length);
			returnValues[0] = splitParameterString[0].substring(1);
			return returnValues;
		}
		else
			return null;
	}
	
	private Object[] getTypeAndVariableNameParameters(String parameterString, String[] splitParameterString)
	{
		if(splitParameterString[1].startsWith("$"))
		{
			int arraySize = 0;
			if(splitParameterString.length > 2)
				arraySize = Integer.parseInt(splitParameterString[2]);
			
			return new Object[] { splitParameterString[0], splitParameterString[1].substring(1), arraySize };
		}
		else
			return null;
	}
	
	private Object[] getParameters(String parameterString, String[] splitParameterString)
	{
		List<Object> parameters = new ArrayList<Object>();
		
		int index = 0;
		while(index < splitParameterString.length)
		{
			Object[] result = getParameterValue(script, splitParameterString, index);
			parameters.add(result[0]);
			index = (int)result[1];
		}
		
		return parameters.toArray();
	}
	
	private static Object[] getParameterValue(AHTMLScript script, String[] parameterStrings, int parameterIndex)
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
			value = new AHTMLScript.VariableReference(parameterStrings[parameterIndex].substring(1));
		
		return new Object[] { value, parameterIndex + indexShift };
	}
	
	private static boolean isIntegerNumber(String string)
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
	
	private static boolean isDoubleNumber(String string)
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
}
