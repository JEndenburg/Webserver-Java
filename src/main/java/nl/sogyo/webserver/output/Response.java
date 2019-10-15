package nl.sogyo.webserver.output;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nl.sogyo.webserver.ContentType;
import nl.sogyo.webserver.HttpStatusCode;

/// Representation of the server HTTP response
public class Response 
{
	private final ZonedDateTime date;
	private final HttpStatusCode statusCode;
	private Map<String, String> customHeaders;
	private final String content;
	private final ContentType contentType;
	
	public Response(HttpStatusCode statusCode, ContentType contentType, String content)
	{
		if(content == null)
			throw new IllegalArgumentException("Content can't be null!");
		this.statusCode = statusCode;
		this.customHeaders = new HashMap<String, String>();
		this.content = content;
		this.contentType = contentType;
		this.date = ZonedDateTime.now(ZoneId.of("GMT"));
	}
	
	public Response(HttpStatusCode statusCode, ContentType contentType)
	{
		this(statusCode, contentType, "");
	}
	
	public void addHeader(String key, String value)
	{
		customHeaders.put(key, value);
	}

    /// HTTP status code that informs the client of the
    /// processing of the request by the server.
    public HttpStatusCode getStatus()
    {
    	return statusCode;
    }

    /// The header parameters and values that are unique for
    /// this response. A response defines a set of headers, some
    /// are unique, others are always present. The date header
    /// is always present and, if the response has content,
    /// so are the Content-Type and Content-Length.
    public Map<String, String> getCustomHeaders()
    {
    	return Collections.unmodifiableMap(customHeaders);
    }

    /// The exact date and time at which this response was
    /// generated. This is used for the date header that is
    /// always added.
    public ZonedDateTime getDate()
    {
    	return date;
    }

    /// Optionally, a response contains content. If we want
    /// to transfer for example a web page, we add the HTML contents
    /// in the body of the response.
    public String getContent()
    {
    	return content;
    }
    
    public ContentType getContentType()
    {
    	return contentType;
    }
}
