package pos.client.http;

import static java.util.Collections.*;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * HTTP client interface 
 */
public interface HTTPClient {

    /**
     * Execute a GET request.
     * @param url      request URL
     * @param params   parameters which will be converted to a query string
     * @param headers  request headers
     * @return         HTTP response object
     */
    HTTPResponse get(String url, Map<String, Object> params, Map<String, String> headers);
    
    /**
     * Execute a GET request with no parameters and headers.
     */
    default HTTPResponse get(String url) {
        return get(url, emptyMap(), emptyMap());
    }
    
    /**
     * Execute a GET request with no headers.
     */
    default HTTPResponse get(String url, Map<String, Object> params) {
        return get(url, params, emptyMap());
    }
    
    /**
     * Execute a POST form-URL-encoded request (submits a form).
     * @param url      request URL
     * @param formData map of keys and values, representing form data
     * @param headers  request headers
     * @return         HTTP response object
     */
    HTTPResponse postForm(String url, Map<String, String> formData, Map<String, String> headers);
    
    /**
     * Execute a POST form-URL-encoded request (submit a form) with empty header map
     */
    default HTTPResponse postForm(String url, Map<String, String> formData) {
        return postForm(url, formData, emptyMap()); 
    }
    
    /**
     * Execute a POST form-URL-encoded request with one or more files
     * @param url            request URL
     * @param fileFieldName  key under which a file will be uploaded, for example "file", or "fileData" 
     * @param files          list of files to upload
     * @param formData       form data
     * @param headers        request headers
     * @return               HTTP response object
     */
    HTTPResponse postFiles(String url, String fileFieldName, List<File> files,
            Map<String, String> formData, Map<String, String> headers);
    
    /**
     * Execute a POST form-URL-encoded request with one or more files with empty header map
     */
    default HTTPResponse postFiles(String url, String fileFieldName, List<File> files,
            Map<String, String> formData) {
        return postFiles(url, fileFieldName, files, formData, Collections.emptyMap());
    }

    /**
     * Execute a GET request for downloading a file
     * @param url           request URL
     * @param params        parameters which will be converted to a query string
     * @param Headers       request headers
     * @param handleStream  consumer for handling the input stream if request succeeds
     * @return              HTTP response object with empty body
     */
    HTTPResponse getFile(String url, Map<String, Object> params, Map<String, String> Headers, Consumer<InputStream> handleStream);

    /**
     * Execute a GET file request with no parameters or headers
     */
    default HTTPResponse getFile(String url, Consumer<InputStream> handleStream) {
        return getFile(url, emptyMap(), emptyMap(), handleStream);
    }

    /**
     * Execute a POST request which content type is application/JSON.
     * @param url      request URL
     * @param json     data in JSON format
     * @param headers  request headers
     * @return         HTTP response object
     */
    HTTPResponse postJson(String url, String json, Map<String, String> headers);

    /**
     * Execute a POST request with content type is application/JSON and empty header map.
     */
    default HTTPResponse postJson(String url, String json) {
        return postJson(url, json, emptyMap());
    }
    
    /**
     * Execute a PUT request which content type is application/JSON
     * @param url      request URL
     * @param json     data in JSON format
     * @param headers  request headers
     * @return         HTTP response object
     */
    HTTPResponse putJson(String url, String json, Map<String, String> headers);
    
    /**
     * Execute a PUT request with content type is application/JSON and empty header map.
     */
    default HTTPResponse putJson(String url, String json) {
        return putJson(url, json, emptyMap());
    }

    /**
     * Execute a DELETE request with headers
     */
    HTTPResponse delete(String url, Map<String, String> headers);
    
    /**
     * Execute a DELETE request without parameters and headers
     */
    default HTTPResponse delete(String url) {
        return delete(url, emptyMap());
    }
    
    /**
     * Get default HTTP client implementation.
     */
    static HTTPClient getClient() {
        return new ApacheHTTPClient();
    }
    
    /**
     * Get HTTP client with credentials info.
     */
    static HTTPClient getClient(String username, String password) {
        return new ApacheHTTPClient(username, password);
    }

    /**
     * An implementation-agnostic representation of HTTP response.
     */
    public final class HTTPResponse {
        
        public static final int OK = 200;
        public static final int BAD_REQUEST = 400;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_SERVER_ERROR = 500;

        private final int statusCode;
        private final String statusText;
        private final String body;
        
        HTTPResponse(int statusCode, String statusText, String body) {
            this.statusCode = statusCode;
            this.statusText = statusText;
            this.body = body;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getStatusText() {
            return statusText;
        }

        public String getBody() {
            return body;
        }
        
        @Override
        public String toString() {
            return "HTTPResponse [statusCode=" + statusCode + ", statusText=" + statusText
                    + ", body=" + body + "]";
        }
        
        static HTTPResponse internalServerError(Exception e) {
            return new HTTPResponse(INTERNAL_SERVER_ERROR, "Internal Server Error", e.toString());
        }

    }
    
    /**
     * This exception happens when HTTP client cannot make a call (it wraps an IO Exception)
     */
    @SuppressWarnings("serial")
    class HTTPClientException extends RuntimeException {
        public HTTPClientException(Exception e) {
            super(e);
        }
    }
    
}
