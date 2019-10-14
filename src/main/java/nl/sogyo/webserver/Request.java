package nl.sogyo.webserver;

import java.util.Map;
import java.util.Set;

/// Representation of the incoming HTTP request.
public class Request 
{
	private final HttpMethod method;
	private final String resourcePath;
	private final Map<String, String> headerParameters;
	private final Map<String, String> urlParameters;
	
	private Request(HttpMethod method, String resourcePath, Map<String, String> headerParameters, Map<String, String> urlParameters)
	{
		this.method = method;
		this.resourcePath = resourcePath;
		this.headerParameters = headerParameters;
		this.urlParameters = urlParameters;
	}
	
    /// Defines the HTTP method that was used by the
    /// client in the incoming request.
    public HttpMethod getHTTPMethod()
    {
    	return method;
    }

    /// Defines the resource path that was requested by
    /// the client. The resource path excludes url parameters.
    public String getResourcePath()
    {
    	return resourcePath;
    }

    /// Defines the names of the header parameters that
    /// were supplied in the request.
    public Set<String> getHeaderParameterNames()
    {
    	return headerParameters.keySet();
    }

    /// Retrieves the supplied header parameter value
    /// correspronding to the name. If no header exists
    /// with the name, it returns null.
    public String getHeaderParameterValue(String name)
    {
    	return headerParameters.get(name);
    }

    /// Retrieves the URL parameters that were present in
    /// the requested URL.
    public Set<String> getParameterNames()
    {
    	return urlParameters.keySet();
    }

    /// Retreives the URL parameter value corresponding to
    /// the name. If no parameter exists with the name,
    /// it returns null.
    public String getParameterValue(String name)
    {
    	return urlParameters.get(name);
    }
}
