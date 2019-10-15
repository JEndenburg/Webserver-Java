package nl.sogyo.webserver.processing;

import nl.sogyo.webserver.processing.methods.*;

public enum AHTMLMethod
{
	PRINT("PRINT", (page, pars) -> {return pars[0] == null ? "null" : pars[0].toString(); })
	;
	
	private final String methodName;
	private final IExecutable executable;
	
	private AHTMLMethod(String methodName, IExecutable executable)
	{
		this.methodName = methodName;
		this.executable = executable;
	}
	
	public static AHTMLMethod fromName(String name)
	{
		for(AHTMLMethod method : AHTMLMethod.values())
		{
			if(method.methodName.equals(name))
				return method;
		}
		return null;
	}
	
	public String execute(AHTMLPage page, Object[] parameters)
	{
		return executable.execute(page, parameters);
	}
}
