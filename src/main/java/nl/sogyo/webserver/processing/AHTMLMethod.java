package nl.sogyo.webserver.processing;

import java.lang.reflect.InvocationTargetException;

import nl.sogyo.webserver.processing.methods.*;

public enum AHTMLMethod
{
	PRINT("PRINT", (script, pars) -> {return pars[0] == null ? "null" : pars[0].toString(); }),
	
	DEFINE("DEFINE", (script, pars) -> {
		AHTMLValueType type = AHTMLValueType.fromName(pars[0].toString());
		Object defaultValue = type.getDefaultValue();
		if(type.isArray())
			defaultValue = new Object[Integer.parseInt((String)pars[2])];
		script.addVariable(pars[1].toString(), defaultValue);
		return null;
		}, ParameterRequestType.TypeAndVariableName),
	
	SET("SET", (script, pars) -> {
		script.addVariable(pars[0].toString(), pars[1]);
		return null;
	}, ParameterRequestType.VariableNameAndValues),
	
	ADD("ADD", (script, pars) -> {
		Object val = script.getVariable(pars[0].toString());
		val = MathHelper.addObjects(val, pars[1]);
		script.addVariable(pars[0].toString(), val);
		return null;
	}, ParameterRequestType.VariableNameAndValues),
	
	SUB("SUB", (script, pars) -> {
		Object val = script.getVariable(pars[0].toString());
		val = MathHelper.subtractObjects(val, pars[1]);
		script.addVariable(pars[0].toString(), val);
		return null;
	}, ParameterRequestType.VariableNameAndValues),

	MUL("MUL", (script, pars) -> {
		Object val = script.getVariable(pars[0].toString());
		val = MathHelper.multiplyObjects(val, pars[1]);
		script.addVariable(pars[0].toString(), val);
		return null;
	}, ParameterRequestType.VariableNameAndValues),

	DIV("DIV", (script, pars) -> {
		Object val = script.getVariable(pars[0].toString());
		val = MathHelper.divideObjects(val, pars[1]);
		script.addVariable(pars[0].toString(), val);
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
	
	public String execute(AHTMLScript script, Object[] parameters)
	{
		return executable.execute(script, parameters);
	}
	
	public static enum ParameterRequestType
	{
		Regular, TypeAndVariableName, VariableNameAndValues,
	}
}
