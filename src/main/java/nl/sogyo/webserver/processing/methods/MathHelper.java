package nl.sogyo.webserver.processing.methods;

public class MathHelper
{
	public static Object divideObjects(Object leftSide, Object rightSide)
	{
		Object returnValue = leftSide;
		
		if(leftSide instanceof Integer)
		{
			if(rightSide instanceof Integer)
				returnValue = (int)leftSide / (int)rightSide;
			else if(rightSide instanceof Double)
				returnValue = (int)((int)leftSide / (double)rightSide);
		}
		else if(leftSide instanceof Double)
		{
			if(rightSide instanceof Integer)
				returnValue = (double)((double)leftSide / (int)rightSide);
			else if(rightSide instanceof Double)
				returnValue = (double)leftSide / (double)rightSide;
		}
		else if(leftSide instanceof String)
		{
			if(rightSide instanceof Integer || rightSide instanceof Double)
				returnValue = ((String)leftSide).substring((int)rightSide);
		}
		
		return returnValue;
	}
	
	public static Object multiplyObjects(Object leftSide, Object rightSide)
	{
		Object returnValue = leftSide;
		
		if(leftSide instanceof Integer)
		{
			if(rightSide instanceof Integer)
				returnValue = (int)leftSide * (int)rightSide;
			else if(rightSide instanceof Double)
				returnValue = (int)((int)leftSide * (double)rightSide);
		}
		else if(leftSide instanceof Double)
		{
			if(rightSide instanceof Integer)
				returnValue = (double)((double)leftSide * (int)rightSide);
			else if(rightSide instanceof Double)
				returnValue = (double)leftSide * (double)rightSide;
		}
		else if(leftSide instanceof String)
		{
			if(rightSide instanceof Integer || rightSide instanceof Double)
				returnValue = ((String)leftSide).repeat((int)rightSide);
		}
		
		return returnValue;
	}
	
	public static Object subtractObjects(Object leftSide, Object rightSide)
	{
		Object returnValue = leftSide;
		
		if(leftSide instanceof Integer)
		{
			if(rightSide instanceof Integer)
				returnValue = (int)leftSide - (int)rightSide;
			else if(rightSide instanceof Double)
				returnValue = (int)((int)leftSide - (double)rightSide);
		}
		else if(leftSide instanceof Double)
		{
			if(rightSide instanceof Integer)
				returnValue = (double)((double)leftSide - (int)rightSide);
			else if(rightSide instanceof Double)
				returnValue = (double)leftSide - (double)rightSide;
		}
		else if(leftSide instanceof String)
		{
			if(rightSide instanceof String)
				returnValue = ((String)leftSide).replaceAll((String)rightSide, "");
			else if(rightSide instanceof Integer || rightSide instanceof Double)
				returnValue = ((String)leftSide).substring(0, ((String)leftSide).length() - (int)rightSide);
		}
		
		return returnValue;
	}
	
	public static Object addObjects(Object leftSide, Object rightSide)
	{
		Object returnValue = leftSide;
		
		if(leftSide instanceof Integer)
		{
			if(rightSide instanceof Integer)
				returnValue = (int)leftSide + (int)rightSide;
			else if(rightSide instanceof Double)
				returnValue = (int)((int)leftSide + (double)rightSide);
		}
		else if(leftSide instanceof Double)
		{
			if(rightSide instanceof Integer)
				returnValue = (double)((double)leftSide + (int)rightSide);
			else if(rightSide instanceof Double)
				returnValue = (double)leftSide + (double)rightSide;
		}
		else if(leftSide instanceof String)
		{
			if(rightSide instanceof String)
				returnValue = (String)leftSide + (String)rightSide;
			else if(rightSide instanceof Integer)
				returnValue = (String)leftSide + ((Integer)rightSide).toString();
			else if(rightSide instanceof Double)
				returnValue = (String)leftSide + ((Double)rightSide).toString();
		}
		
		return returnValue;
	}
}
