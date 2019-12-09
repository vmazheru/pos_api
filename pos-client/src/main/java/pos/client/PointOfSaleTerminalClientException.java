package pos.client;

import pos.client.http.HTTPClient.HTTPResponse;

/**
 * Report unexpected HTTP statuses returned to the POS service client.
 */
@SuppressWarnings("serial")
public class PointOfSaleTerminalClientException extends RuntimeException {
    
    private final int httpStatus;
    
    public PointOfSaleTerminalClientException(HTTPResponse r) {
        super("HTTP status: " + r.getStatusCode() + ", HTTP response: " + r.getBody());
        httpStatus = r.getStatusCode();
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }

}
