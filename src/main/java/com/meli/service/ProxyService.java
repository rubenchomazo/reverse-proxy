package com.meli.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

@Service
public class ProxyService {
    String domain = System.getenv("PROXY-TARGET");
    //    String domain = "api.mercadolibre.com";
    private final static Logger logger = LogManager.getLogger(ProxyService.class);

    @Retryable(exclude = {HttpStatusCodeException.class}, include = Exception.class, backoff = @Backoff(delay = 5000, multiplier = 1.0), maxAttempts = 1)
    public ResponseEntity<String> processProxyRequest(String body, HttpMethod method, HttpServletRequest request, HttpServletResponse response, String traceId) throws URISyntaxException {
        ThreadContext.put("traceId", traceId);
        String requestUrl = request.getRequestURI();
        URI uri = new URI("https", null, domain, -1, null, null, null);
        uri = UriComponentsBuilder.fromUri(uri).path(requestUrl).query(request.getQueryString()).build(true).toUri();
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        logger.info("Proxy target " + uri.getHost());
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.set(headerName, request.getHeader(headerName));
        }
        headers.set("TRACE", traceId);
        headers.remove(HttpHeaders.ACCEPT_ENCODING);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        try {
            ResponseEntity<String> serverResponse = restTemplate.exchange(uri, method, httpEntity, String.class);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.put(HttpHeaders.CONTENT_TYPE, serverResponse.getHeaders().get(HttpHeaders.CONTENT_TYPE));
            logger.info(serverResponse);
            return serverResponse;
        } catch (HttpStatusCodeException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(e.getRawStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
        }

    }
}
