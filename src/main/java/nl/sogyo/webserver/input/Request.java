package nl.sogyo.webserver.input;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nl.sogyo.webserver.HttpMethod;

/// Representation of the incoming HTTP request.
public class Request 
{
	private final HttpMethod method;
	private final String resourcePath;
	private final Map<String, String> headerParameters;
	private final Map<String, String> urlParameters;
	private final String rawBody;
	
	private Request(HttpMethod method, String resourcePath, Map<String, String> headerParameters, Map<String, String> urlParameters, String rawBody)
	{
		this.method = method;
		this.resourcePath = resourcePath;
		this.headerParameters = headerParameters;
		this.urlParameters = urlParameters;
		this.rawBody = rawBody;
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
    
    @Override
    public String toString()
    {
    	return String.format("%s %s\nURL Params: %s\nHeader Params: %s\nContent:\n%s", method.toString(), resourcePath.toString(), urlParameters.toString(), headerParameters.toString(), rawBody.toString());
    }
    
    public static class Builder
    {
    	private HttpMethod httpMethod;
    	private String url;
    	private Map<String, String> headerParameters = new HashMap<String, String>();
    	private String rawBody;
    	
    	public void setHttpMethod(String httpMethod)
    	{
    		this.httpMethod = HttpMethod.parse(httpMethod);
    	}
    	
    	public void setUrl(String url)
    	{
    		this.url = url;
    	}
    	
    	public void addHeader(String key, String value)
    	{
    		headerParameters.put(key, value);
    	}
    	
    	public void setBody(String body)
    	{
    		this.rawBody = body;
    	}
    	
    	private Map<String, String> getUrlParameterMap(String parameterString)
    	{
    		Map<String, String> urlParameterMap = new HashMap<String, String>();
    		if(parameterString.isBlank())
    			return urlParameterMap;
    		
    		String[] parameterKVPairs = parameterString.replace("%20", " ").split("&");
    		for(String kvPairConcat : parameterKVPairs)
    		{
    			String[] kvPairSplit = kvPairConcat.split("=");
    			if(kvPairSplit.length == 2)
    				urlParameterMap.put(kvPairSplit[0], kvPairSplit[1]);
    		}
    		
    		return urlParameterMap;
    	}
    	
    	public Request build()
    	{
    		String[] resourcePathAndParameters = url.split("\\?");
    		Map<String, String> urlParameters = resourcePathAndParameters.length > 1 ? getUrlParameterMap(resourcePathAndParameters[1]) : new HashMap<String, String>();
    		return new Request(httpMethod, resourcePathAndParameters[0], headerParameters, urlParameters, rawBody);
    	}
    }
}
