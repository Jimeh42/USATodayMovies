package challenge.gannett.usatodaymovies;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class JSONHelper {

	/*
	 * Retrieve JSON content from a URL and parse it using Jackson
	 * 
     * @param url The exact URL to request.
     * @return Parsed JSON content returned by the server.
     * @throws IOException if any connection or server error occurs.
	 */
    @SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getJSONContent(String url) throws IOException {
        // Create client and set our specific user-agent string
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);

        // Check if server response is valid
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() != 200) {
            throw new IOException("Invalid response from server: " +
                                   status.toString());
        }

        // Pull content stream from response
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> content = (Map<String, Object>)mapper.readValue(inputStream, Map.class);
        List<Map<String, Object>> result = (List<Map<String, Object>>)content.get("MovieReviews");
    	return result;
    }
    
    /**
     * Pull the raw text content of the given URL.
     *
     * @param url The exact URL to request.
     * @return The raw content returned by the server.
     * @throws IOException if any connection or server error occurs.
     */
    protected static String getUrlContent(String url) 
        throws IOException
    {
        byte[] sBuffer = new byte[512];

        // Create client and set our specific user-agent string
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        HttpResponse response = client.execute(request);

        // Check if server response is valid
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() != 200) {
            throw new IOException("Invalid response from server: " +
                                   status.toString());
        }

        // Pull content stream from response
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();

        ByteArrayOutputStream content = new ByteArrayOutputStream();

        // Read response into a buffered stream
        int readBytes = 0;
        while ((readBytes = inputStream.read(sBuffer)) != -1) {
            content.write(sBuffer, 0, readBytes);
        }

        // Return result from buffered stream
        return new String(content.toByteArray());
    }

}
