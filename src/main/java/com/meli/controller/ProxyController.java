package com.meli.controller;

import com.meli.service.ProxyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.UUID;

@RestController
public class ProxyController {

    @Autowired
    ProxyService service;

    @RequestMapping("/**")
    public ResponseEntity<String> sendRequestToSPM(@RequestBody(required = false) String body, HttpMethod method, HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
        return service.processProxyRequest(body, method, request, response, UUID.randomUUID().toString());
    }
}
