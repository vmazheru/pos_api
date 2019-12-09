package pos.client.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Implementation of HTTP client interface with Apache HTTP client 4.5.x
 */
public final class ApacheHTTPClient implements HTTPClient {
    
    private final CredentialsProvider credentialsProvider;
    
    public ApacheHTTPClient() {
        credentialsProvider = null;
    }
    
    public ApacheHTTPClient(String username, String password) {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, credentials);
        this.credentialsProvider = provider;
    }

    @Override
    public HTTPResponse get(String url, Map<String, Object> parameters, Map<String, String> headers) {
        HttpGet req = new HttpGet(url + toQueryString(parameters));
        return execute(req, headers, ApacheHTTPClient::toResponse);
    }
    
    @Override
    public HTTPResponse postForm(String url, Map<String, String> formData, Map<String, String> headers) {
        HttpPost req = new HttpPost(url);
        List<NameValuePair> params = formData.entrySet().stream()
                .map(e -> new BasicNameValuePair(e.getKey(), e.getValue())).collect(toList());
        req.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
        req.addHeader("Content-Type", "application/x-www-form-urlencoded");
        return execute(req, headers, ApacheHTTPClient::toResponse);
    }
    
    @Override
    public HTTPResponse postFiles(String url, String fileFieldName, List<File> files,
            Map<String, String> formData, Map<String, String> headers) {
        HttpPost req = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (Map.Entry<String, String> e : formData.entrySet()) {
            builder.addTextBody(e.getKey(), e.getValue());
        }
        
        for (File f : files) {
            builder.addBinaryBody(fileFieldName, f);
        }

        req.setEntity(builder.build());
        return execute(req, headers, ApacheHTTPClient::toResponse);
    }

    @Override
    public HTTPResponse getFile(String url, Map<String, Object> parameters, Map<String, String> headers,
                        Consumer<InputStream> handleStream) {
        HttpGet req = new HttpGet(url + toQueryString(parameters));
        return execute(req, headers, response -> {
            StatusLine statusLine = response.getStatusLine();
            try {
                if(statusLine.getStatusCode() == HttpStatus.SC_OK && response.getEntity() != null) {
                    try(InputStream input = response.getEntity().getContent()) {
                        handleStream.accept(input);
                    }
                    return new HTTPResponse(statusLine.getStatusCode(), statusLine.getReasonPhrase(), "");
                }
                return toResponse(response);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @Override
    public HTTPResponse postJson(String url, String json, Map<String, String> headers) {
        HttpPost req = new HttpPost(url);
        StringEntity body = new StringEntity(json, StandardCharsets.UTF_8);
        req.setEntity(body);
        req.addHeader("Content-Type", "application/json");
        return execute(req, headers, ApacheHTTPClient::toResponse);
    }

    @Override
    public HTTPResponse putJson(String url, String json, Map<String, String> headers) {
        HttpPut req = new HttpPut(url);
        StringEntity body = new StringEntity(json, StandardCharsets.UTF_8);
        req.setEntity(body);
        req.addHeader("Content-Type", "application/json");
        return execute(req, headers, ApacheHTTPClient::toResponse);
    }

    @Override
    public HTTPResponse delete(String url, Map<String, String> headers) {
        HttpDelete req = new HttpDelete(url);
        return execute(req, headers, ApacheHTTPClient::toResponse);
    }
    
    private CloseableHttpClient getClient() {
        int timeout = 60;
        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setConnectTimeout(timeout * 1000)
                .build();

        HttpClientBuilder builder = HttpClients.custom().setDefaultRequestConfig(config);
        if (credentialsProvider != null) {
            builder.setDefaultCredentialsProvider(credentialsProvider);
        }
        return builder.build();
    }

    private <T>T execute(HttpUriRequest req, Map<String, String> headers, Function<CloseableHttpResponse, T> handleResponse) {
        for (Map.Entry<String, String> e : headers.entrySet()) {
            req.addHeader(e.getKey(), e.getValue());
        }

        try (CloseableHttpClient client = getClient()) {
            CloseableHttpResponse res = client.execute(req);
            return handleResponse.apply(res);
        } catch (IOException | UncheckedIOException e) {
            // only happens when the HTTP call could not succeed
            throw new HTTPClientException(e);
        }
    }

    private static HTTPResponse toResponse(CloseableHttpResponse r) {
        try {
            StatusLine statusLine = r.getStatusLine();
            HttpEntity e = r.getEntity();
            String body = e != null ? EntityUtils.toString(e, StandardCharsets.UTF_8) : null;
            return new HTTPResponse(statusLine.getStatusCode(), statusLine.getReasonPhrase(), body);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String toQueryString(Map<String, Object> params) {
        if (params.isEmpty()) return "";
        
        String queryString = "?" + params.entrySet().stream().map(e -> {
            String key = e.getKey();
            Object value = e.getValue();
            if (value instanceof Collection) {
                @SuppressWarnings("unchecked")
                Collection<Object> values = (Collection<Object>) value;
                return values.stream().map(v -> encode(key) + "=" + encode(v.toString())).collect(joining("&"));
            }
            return encode(key) + "=" + encode(value.toString());
        }).collect(joining("&"));
        
        return queryString;
    }
    
    private static String encode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
