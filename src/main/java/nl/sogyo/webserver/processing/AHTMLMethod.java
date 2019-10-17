package nl.sogyo.webserver.processing;

import nl.sogyo.webserver.processing.methods.*;

public enum AHTMLMethod
{
	PRINT("PRINT", (script, pars) -> {return pars[0] == null ? "null" : pars[0].toString().replace("\\n", "\n"); }),
	
	DEFINE("DEFINE", (script, pars) -> {
		AHTMLValueType type = AHTMLValueType.fromName(pars[0].toString());
		Object defaultValue = type.getDefaultValue();
		if(type.isArray())
		{
			if(pars.length > 2)
				defaultValue = new Object[Integer.parseInt(pars[2].toString())];
			else
				defaultValue = new Object[0];
		}
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
	
	ISNULL("ISNULL", (script, pars) -> {
		script.skipNextLine = pars[0] != null;
		return null;
	}),
	
	ISNOTNULL("ISNOTNULL", (script, pars) -> {
		script.skipNextLine = pars[0] == null;
		return null;
	}),
	
	ISEQUAL("ISEQUAL", (script, pars) -> {
		script.skipNextLine = !MathHelper.objectsEqual(pars[0], pars[1]);
		return null;
	}),
	
	ISNOTEQUAL("ISNOTEQUAL", (script, pars) -> {
		script.skipNextLine = MathHelper.objectsEqual(pars[0], pars[1]);
		return null;
	}),
	
	ISABOVE("ISABOVE", (script, pars) -> {
		script.skipNextLine = !MathHelper.objectsGreaterThan(pars[0], pars[1]);
		return null;
	}),
	
	ISBELOW("ISBELOW", (script, pars) -> {
		script.skipNextLine = !MathHelper.objectsLessThan(pars[0], pars[1]);
		return null;
	}),
	
	LABEL("LABEL", (script, pars) -> {
		script.addLabel(pars[0].toString());
		return null;
	}),
	
	CALL("CALL", (script, pars) -> {
		return script.executeFromPosition(script.getLabelPosition(pars[0].toString()));
	}),
	
	DEBUG("DEBUG", (script, pars) -> {
		System.out.println("[DEBUG] " + java.util.Arrays.asList(pars));
		return null;
	}),
	
	STARTINIT("STARTINIT", (script, pars) -> {
		script.init = true;
		return null;
	}),
	
	ENDINIT("ENDINIT", (script, pars) -> {
		script.init = false;
		return null;
	}),
	
	ENDCALL("ENDCALL", (script, pars) -> null),
	
	ARRAY_ADD("ARRAY_ADD", (script, pars) -> {
		Object[] srcArray = (Object[]) script.getVariable(pars[0].toString());
		Object[] dstArray = new Object[srcArray.length + 1];
		System.arraycopy(srcArray, 0, dstArray, 0, srcArray.length);
		dstArray[dstArray.length - 1] = pars[1];
		script.addVariable(pars[0].toString(), dstArray);
		return null;
	}, ParameterRequestType.VariableNameAndValues),
	
	ARRAY_REMOVEAT("ARRAY_REMOVEAT", (script, pars) -> {
		Object[] srcArray = (Object[]) script.getVariable(pars[0].toString());
		Object[] dstArray = new Object[srcArray.length - 1];
		int removedId = Integer.parseInt(pars[1].toString());
		
		for(int i = 0, j = 0; i < srcArray.length; i++)
		{
			if(i != removedId)
			{
				dstArray[j] = srcArray[i];
				j++;
			}
		}
		
		script.addVariable(pars[0].toString(), dstArray);
		return null;
	}, ParameterRequestType.VariableNameAndValues),
	
	ARRAY_INSERT("ARRAY_INSERT", (script, pars) -> {
		Object[] srcArray = (Object[]) script.getVariable(pars[0].toString());
		Object[] dstArray = new Object[srcArray.length + 1];
		int insertId = Integer.parseInt(pars[1].toString());
		
		for(int i = 0, j = 0; i < srcArray.length; j++)
		{
			if(j == insertId)
				dstArray[j] = pars[2];
			else
			{
				dstArray[j] = srcArray[i];
				i++;
			}
		}
		
		script.addVariable(pars[0].toString(), dstArray);
		return null;
	}, ParameterRequestType.VariableNameAndValues),
	
	GIVE_ARRAY_LENGTH("GIVE_ARRAY_LENGTH", (script, pars) -> {
		Object[] array = (Object[])pars[1];
		script.addVariable(pars[0].toString(), array.length);
		return null;
	}, ParameterRequestType.VariableNameAndValues),
	
	GIVE_ARRAY_VALUE("GIVE_ARRAY_VALUE", (script, pars) -> {
		Object[] array = (Object[])pars[1];
		Object value = null;
		int index = (int)pars[2];
		if(index < array.length)
			value = array[index];
		
		script.addVariable(pars[0].toString(), value);
		return null;
	}, ParameterRequestType.VariableNameAndValues),
	
	GIVE_STATIC("GIVE_STATIC", (script, pars) -> {
		Object staticValue = script.getStaticVariable(pars[1].toString());
		script.addVariable(pars[0].toString(), staticValue);
		return null;
	}, ParameterRequestType.VariableNameAndValues),
	
	TAKE_STATIC("TAKE_STATIC", (script, pars) -> {
		Object localValue = script.getVariable(pars[0].toString());
		script.addStaticVariable(pars[1].toString(), localValue);
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
