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
		}, ParameterRequestType.TypeAndVariableName),
	
	SET("SET", (page, pars) -> {
		page.addVariable(pars[0].toString(), pars[1]);
		return null;
	}, ParameterRequestType.VariableNameAndValues),
	
	ADD("ADD", (page, pars) -> {
		Object val = page.getVariable(pars[0].toString());
		val = MathHelper.addObjects(val, pars[1]);
		page.addVariable(pars[0].toString(), val);
		return null;
	}, ParameterRequestType.VariableNameAndValues),
	
	SUB("SUB", (page, pars) -> {
		Object val = page.getVariable(pars[0].toString());
		val = MathHelper.subtractObjects(val, pars[1]);
		page.addVariable(pars[0].toString(), val);
		return null;
	}, ParameterRequestType.VariableNameAndValues),

	MUL("MUL", (page, pars) -> {
		Object val = page.getVariable(pars[0].toString());
		val = MathHelper.multiplyObjects(val, pars[1]);
		page.addVariable(pars[0].toString(), val);
		return null;
	}, ParameterRequestType.VariableNameAndValues),

	DIV("DIV", (page, pars) -> {
		Object val = page.getVariable(pars[0].toString());
		val = MathHelper.divideObjects(val, pars[1]);
		page.addVariable(pars[0].toString(), val);
		return null;
	}, ParameterRequestType.VariableNameAndValues),
	
	;
	
	private final String methodName;
	private final IExecutable executable;
	private final ParameterRequestType parameterRequestType;
	
	private AHTMLMethod(String methodName, IExecutable executable, ParameterRequestType parameterRequestType)
	{
		this.methodName = methodName;
		this.executable = executable;
		this.parameterRequestType = parameterRequestType;
	}
	
	private AHTMLMethod(String methodName, IExecutable executable)
	{
		this(methodName, executable, ParameterRequestType.Regular);
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
	
	public ParameterRequestType getParameterRequestType()
	{
		return parameterRequestType;
	}
	
	public String execute(AHTMLPage page, Object[] parameters)
	{
		return executable.execute(page, parameters);
	}
	
	public static enum ParameterRequestType
	{
		Regular, TypeAndVariableName, VariableNameAndValues,
	}
}
