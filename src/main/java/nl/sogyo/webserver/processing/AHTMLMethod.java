package nl.sogyo.webserver.processing;

import java.lang.reflect.InvocationTargetException;

import nl.sogyo.webserver.processing.methods.*;

public enum AHTMLMethod
{
	PRINT("PRINT", (page, pars) -> {return pars[0] == null ? "null" : pars[0].toString(); }),
	
	DEFINE("DEFINE", (page, pars) -> {
		AHTMLValueType type = AHTMLValueType.fromName(pars[0].toString());
		Object defaultValue = type.getDefaultValue();
		if(type.isArray())
			defaultValue = new Object[Integer.parseInt((String)pars[2])];
		page.addVariable(pars[1].toString(), defaultValue);
		return null;
		}),
	
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
