package nl.sogyo.webserver.input;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RequestReader
{
    private static final String CONTENT_LENGTH_HEADER_KEY = "content-length";
    private ParsingPhase currentPhase = ParsingPhase.MethodCall;
    private int contentLength = -1;
    
    private RequestReader() {}
    
	public static Request parseRequestFromStream(InputStream inputStream) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		return new RequestReader().parseRequest(reader);
	}
	
    private Request parseRequest(BufferedReader reader) throws IOException
    {
    	currentPhase = ParsingPhase.MethodCall;
    	Request.Builder requestBuilder = new Request.Builder();
    	parseRequest(reader, requestBuilder);
    	return requestBuilder.build();
    }
    
    private void parseRequest(BufferedReader reader, Request.Builder requestBuilder) throws IOException
    {
    	do
    	{
    		switch(currentPhase)
    		{
    		case MethodCall:
    			parseMethodCall(reader.readLine(), requestBuilder);
    			break;
    		case Headers:
    			parseHeader(reader.readLine(), requestBuilder);
    			break;
    		case Body:
    			parseContentBody(reader, requestBuilder);
    			break;
			default:
				currentPhase = ParsingPhase.Unknown;
				break;
    		}
    	}
    	while(currentPhase != ParsingPhase.End && currentPhase != ParsingPhase.Unknown);
    }
    
    private void parseMethodCall(String string, Request.Builder requestBuilder)
    {
    	String[] methodCallParts = string.split(" ");
		requestBuilder.setHttpMethod(methodCallParts[0]);
		requestBuilder.setUrl(methodCallParts[1]);
		currentPhase = ParsingPhase.Headers;
    }
    
    private void parseHeader(String string, Request.Builder requestBuilder)
    {
    	if(!string.isEmpty())
    	{
	    	String[] kvPair = getHeaderKV(string);
	    	if(kvPair != null)
	    	{
	    		requestBuilder.addHeader(kvPair[0], kvPair[1]);
	    		if(kvPair[0].equalsIgnoreCase(CONTENT_LENGTH_HEADER_KEY))
	    			contentLength = Integer.parseInt(kvPair[1]);
	    	}
    	}
    	else
    		currentPhase = contentLength > 0 ? ParsingPhase.Body : ParsingPhase.End;
	}
    
    private String[] getHeaderKV(String line)
    {
    	String[] kv = line.split(":");
    	if(kv.length == 2)
    	{
    		kv[0] = kv[0].strip();
    		kv[1] = kv[1].strip();
    		return kv;
    	}
    	else
    		return null;
    }
    
    private void parseContentBody(BufferedReader reader, Request.Builder requestBuilder) throws IOException
    {
    	StringBuilder bodyBuilder = new StringBuilder();
    	int readChar;
    	for(
    			int i = 0;
    			i < contentLength
    			&& (readChar = reader.read()) != -1;
    			i++)
    	{
    		bodyBuilder.append((char)readChar);
    	}
    	requestBuilder.setBody(bodyBuilder.toString());
    	currentPhase = ParsingPhase.End;
    }
    
    private static enum ParsingPhase
    {
    	MethodCall,
    	Headers,
    	Body,
    	End,
    	Unknown,
    }
}
