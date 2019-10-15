package nl.sogyo.webserver.processing.methods;

import nl.sogyo.webserver.processing.AHTMLPage;

public interface IExecutable
{
	String execute(AHTMLPage page, Object[] parameters);
}
