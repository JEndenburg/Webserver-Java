package nl.sogyo.webserver;

/// The HTTP method that was used for the request by the client.
public enum HttpMethod 
{
    GET, POST, PUT, DELETE;
	
	public static HttpMethod parse(String str)
	{
		switch(str.strip().toUpperCase())
		{
		case "GET": return HttpMethod.GET;
		case "POST": return HttpMethod.POST;
		case "PUT": return HttpMethod.PUT;
		case "DELETE": return HttpMethod.DELETE;
		default: throw new IllegalArgumentException(String.format("\"%s\" is not a supported HTTP method.", str.strip().toUpperCase()));
		}
	}
}
