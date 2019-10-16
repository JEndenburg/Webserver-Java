package nl.sogyo.webserver.processing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AHTMLScript
{
	private Map<String, Object> variableMap = new HashMap<>();
	private List<MethodCall> script = new ArrayList<MethodCall>();
	public boolean skipNextLine = false;
	
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
	
	public String executeMethod(MethodCall methodCall)
	{
		script.add(methodCall);
		if(skipNextLine)
		{
			skipNextLine = false;
			return null;
		}
		else
		{
			try
			{
				return methodCall.execute(this);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public static class MethodCall
	{
		private final AHTMLMethod method;
		private final Object[] parameters;
		private final String[] parameterNames;
		
		public MethodCall(AHTMLMethod method, Object[] parameters, String[] parameterNames)
		{
			this.method = method;
			this.parameters = parameters;
			this.parameterNames = parameterNames;
		}
		
		public String execute(AHTMLScript script)
		{
			List<Object> parameterValues = new ArrayList<Object>();
			
			for(int i = 0; i < parameters.length; i++)
			{
				if(parameters[i] instanceof VariableReference)
				{
					Object[] valueAndShift = ((VariableReference)parameters[i]).getValue(script, parameterNames, i);
					parameterValues.add(valueAndShift[0]);
					i += (int)valueAndShift[1];
				}
				else
					parameterValues.add(parameters[i]);
			}
						
			return method.execute(script, parameterValues.toArray());
		}
	}
	
	public static class VariableReference
	{
		private final String variableName;
		
		public VariableReference(String variableName)
		{
			this.variableName = variableName;
		}
		
		public Object[] getValue(AHTMLScript script, String[] parameterStrings, int currentIndex)
		{
			return AHTMLProcessor.getVariableValue(script, script.getVariable(variableName), parameterStrings, currentIndex);
		}
	}
}
