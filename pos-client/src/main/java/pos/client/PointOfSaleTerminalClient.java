package pos.client;

import java.math.BigDecimal;
import java.util.Collections;

import cl.json.JsonMapper;
import pos.client.http.ApacheHTTPClient;
import pos.client.http.HTTPClient;
import pos.client.http.HTTPClient.HTTPResponse;
import pos.model.PointOfSaleTerminal;
import pos.model.Pricing;

/**
 * HTTP Java client for POS terminal service. It implements the same {@link PointOfSaleTerminal} interface,
 * but plainly delegates its functionality to the REST service.
 * 
 * The client throws {@link PointOfSaleTerminalClientException} when the HTTP status is not OK.
 */
public class PointOfSaleTerminalClient implements PointOfSaleTerminal {
    
    private static final String HOST = "http://localhost:8080"; // normally is not hard-coded, but comes from configuration
    private static final String URN_TERMINAL = "/terminal";
    private static final String URN_TERMINAL_ACTIVATE = URN_TERMINAL + "/activate";
    private static final String URN_PRICING = "/pricing";
    private static final String URN_SCAN = "/scan";
    private static final String URN_TOTAL = "/total";
    
    private static final int HTTP_STATUS_OK = 200;
    
    private final String id;
    private final HTTPClient httpClient;
    private final JsonMapper jsonMapper;
    
    public PointOfSaleTerminalClient() {
        httpClient = new ApacheHTTPClient();
        jsonMapper = JsonMapper.getJsonMapper();
        HTTPResponse r = httpClient.postForm(HOST + URN_TERMINAL_ACTIVATE, Collections.emptyMap());
        id = r.getBody();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setPricing(Pricing... pricings) {
        HTTPResponse r = httpClient.putJson(url(URN_PRICING), jsonMapper.toJson(pricings));
        checkResponse(r);
    }

    @Override
    public void scan(String productCode) {
        HTTPResponse r = httpClient.putJson(url(URN_SCAN), jsonMapper.toJson(productCode));
        checkResponse(r);
    }

    @Override
    public BigDecimal calculateTotal() {
        HTTPResponse r = httpClient.postForm(url(URN_TOTAL), Collections.emptyMap());
        checkResponse(r);
        return new BigDecimal(r.getBody());
    }
    
    private String url(String urn) {
        return HOST + URN_TERMINAL + "/" + id + urn;
    }
    
    private static void checkResponse(HTTPResponse r) {
        if (r.getStatusCode() != HTTP_STATUS_OK) {
            throw new PointOfSaleTerminalClientException(r);
        }
    }

}
