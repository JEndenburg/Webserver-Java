package nl.sogyo.webserver.processing.methods;

import nl.sogyo.webserver.processing.AHTMLScript;

public interface IExecutable
{
	String execute(AHTMLScript script, Object[] parameters);
}
