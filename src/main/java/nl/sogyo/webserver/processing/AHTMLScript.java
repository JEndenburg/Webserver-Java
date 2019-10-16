package nl.sogyo.webserver.processing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AHTMLScript
{
	private Map<String, Object> variableMap = new HashMap<>();
	private List<MethodCall> script = new ArrayList<>();
	private Map<String, Integer> labels = new HashMap<>();
	private boolean rerun = false;
	public boolean skipNextLine = false;
	public boolean init = false;
	
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
	
	public int getPosition()
	{
		return script.size() - 1;
	}
	
	public void addLabel(String labelName)
	{
		if(!rerun)
			labels.put(labelName, getPosition());
	}
	
	public int getLabelPosition(String labelName)
	{
		return labels.get(labelName);
	}
	
	public String executeFromPosition(int position)
	{
		rerun = true;
		StringBuilder sb = new StringBuilder();
		for(; position < script.size(); position++)
		{
			if(!skipNextLine)
			{
				MethodCall methodCall = script.get(position);
				if(methodCall.method == AHTMLMethod.BREAK)
					return sb.toString();
				
				String output = script.get(position).execute(this);
				if(output != null)
					sb.append(output);
			}
			else
				skipNextLine = false;
		}
		rerun = false;
		return sb.toString();
	}
	
	public String executeMethod(MethodCall methodCall)
	{
		script.add(methodCall);
		if(skipNextLine || (init && (methodCall.method != AHTMLMethod.ENDINIT && methodCall.method != AHTMLMethod.LABEL)))
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
			catch(Throwable e)
			{
				//e.printStackTrace();
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
			Object rawValue = script.getVariable(variableName);
			if(rawValue == null)
				return new Object[] { null, 0 };
			else
				return AHTMLProcessor.getVariableValue(script, script.getVariable(variableName), parameterStrings, currentIndex);
		}
	}
}
